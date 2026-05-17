package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.*;
import cn.pzhdv.blog.mapper.*;
import cn.pzhdv.blog.service.ArticleService;
import cn.pzhdv.blog.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * 核心优化：批量查询、杜绝N+1、仅查询必要字段
 *
 * @author PanZonghui
 * @since 2025-06-25
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagRelationMapper articleTagRelationMapper;
    private final ArticleCategoryRelationMapper articleCategoryRelationMapper;
    private final ArticleCategoryMapper articleCategoryMapper;
    private final ArticleTagMapper articleTagMapper;

    /**
     * 根据发布状态统计文章总数
     *
     * @param publishState 发布状态：true-已发布，false-未发布
     * @return 文章数量
     */
    @Override
    public Long queryArticleTotal(Boolean publishState) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getPublishState, publishState);
        return articleMapper.selectCount(wrapper);
    }

    /**
     * 查询文章发布日期列表（用于归档）
     * 只查询create_time字段，性能最优
     *
     * @param publishState 发布状态
     * @return 文章创建时间列表
     */
    @Override
    public List<Date> queryArticlePublishDateList(Boolean publishState) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Article::getPublishState, publishState);
        wrapper.select(Article::getCreateTime);

        List<Article> articles = articleMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(articles)) {
            return Collections.emptyList();
        }

        // 简化写法
        return articles.stream()
                .map(Article::getCreateTime)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询单篇文章详情
     * 批量一次性加载 分类 + 标签，无N+1
     *
     * @param articleId 文章ID
     * @return 文章详情（含分类、标签）
     */
    @Override
    public Article queryArticleById(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return null;
        }
        setArticleCategoriesBatch(Collections.singletonList(article));
        setArticleTagsBatch(Collections.singletonList(article));
        return article;
    }

    /**
     * 移动端首页文章分页查询（按推荐权重排序）
     * 支持：关键词、日期筛选、标签筛选、发布状态
     */
    @Override
    public Page<Article> queryMobileHomePageArticleList(String keyword, Date publishDate, Integer articleTagId,
                                                        Integer pageNum, Integer pageSize, Boolean publishState) {
        Page<Article> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Article::getArticleId, Article::getImage, Article::getTitle,
                        Article::getExcerpt, Article::getCreateTime)
                .orderByDesc(Article::getRecommendWeight, Article::getArticleId);

        // 关键词搜索（优化判空）
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Article::getTitle, keyword.trim());
        }

        // 按日期范围
        if (publishDate != null) {
            Date start = DateUtils.startOfDay(publishDate);
            Date end = DateUtils.endOfDay(publishDate);
            wrapper.ge(Article::getCreateTime, start)
                    .le(Article::getCreateTime, end);
        }

        // 发布状态
        if (publishState != null) {
            wrapper.eq(Article::getPublishState, publishState);
        }

        // 根据标签ID过滤
        if (articleTagId != null) {
            List<Integer> articleIds = getArticleIdsByTagId(articleTagId);
            if (CollectionUtils.isEmpty(articleIds)) {
                return page;
            }
            wrapper.in(Article::getArticleId, articleIds);
        }

        Page<Article> resultPage = articleMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(resultPage.getRecords())) {
            return resultPage;
        }

        // 批量赋值：分类 + 标签
//        setArticleCategoriesBatch(resultPage.getRecords());
//        setArticleTagsBatch(resultPage.getRecords());

        return resultPage;
    }

    /**
     * 移动端分类页文章分页查询
     */
    @Override
    public Page<Article> queryMobileCategoryPageArticleList(List<Integer> categoryIds,
                                                            Integer pageNum, Integer pageSize, Boolean publishState) {
        Page<Article> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Article::getArticleId, Article::getImage, Article::getTitle,
                        Article::getExcerpt, Article::getCreateTime)
                .orderByDesc(Article::getRecommendWeight, Article::getArticleId);

        if (publishState != null) {
            wrapper.eq(Article::getPublishState, publishState);
        }

        if (!CollectionUtils.isEmpty(categoryIds)) {
            List<Integer> articleIds = getArticleIdsByCategoryIds(categoryIds);
            if (CollectionUtils.isEmpty(articleIds)) {
                return page;
            }
            wrapper.in(Article::getArticleId, articleIds);
        }

        Page<Article> resultPage = articleMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(resultPage.getRecords())) {
            return resultPage;
        }

        setArticleCategoriesBatch(resultPage.getRecords());
//        setArticleTagsBatch(resultPage.getRecords());

        return resultPage;
    }

    // ==================== 核心批量优化方法 ====================

    /**
     * 批量为文章列表设置【分类信息】
     */
    private void setArticleCategoriesBatch(List<Article> articleList) {
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }

        List<Integer> articleIds = articleList.stream()
                .map(Article::getArticleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<ArticleCategoryRelation> rWrapper = new LambdaQueryWrapper<>();
        rWrapper.in(ArticleCategoryRelation::getArticleId, articleIds);
        List<ArticleCategoryRelation> relations = articleCategoryRelationMapper.selectList(rWrapper);

        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        Map<Integer, List<Integer>> articleToCateIds = relations.stream()
                .collect(Collectors.groupingBy(
                        ArticleCategoryRelation::getArticleId,
                        Collectors.mapping(ArticleCategoryRelation::getCategoryId, Collectors.toList())
                ));

        Set<Integer> cateIds = relations.stream()
                .map(ArticleCategoryRelation::getCategoryId)
                .collect(Collectors.toSet());

        List<ArticleCategory> cateList = articleCategoryMapper.selectByIds(cateIds);
        Map<Integer, ArticleCategory> cateMap = cateList.stream()
                .collect(Collectors.toMap(ArticleCategory::getCategoryId, c -> c));

        for (Article article : articleList) {
            List<Integer> cIds = articleToCateIds.getOrDefault(article.getArticleId(), Collections.emptyList());
            List<ArticleCategory> cs = cIds.stream()
                    .filter(cateMap::containsKey)
                    .map(cateMap::get)
                    .collect(Collectors.toList());

            article.setCategoryIds(cIds);
            article.setArticleCategoryList(cs);
        }
    }

    /**
     * 批量为文章列表设置【标签信息】
     */
    private void setArticleTagsBatch(List<Article> articleList) {
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }

        List<Integer> articleIds = articleList.stream()
                .map(Article::getArticleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<ArticleTagRelation> rWrapper = new LambdaQueryWrapper<>();
        rWrapper.in(ArticleTagRelation::getArticleId, articleIds);
        List<ArticleTagRelation> relations = articleTagRelationMapper.selectList(rWrapper);

        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        Map<Integer, List<Integer>> articleToTagIds = relations.stream()
                .collect(Collectors.groupingBy(
                        ArticleTagRelation::getArticleId,
                        Collectors.mapping(ArticleTagRelation::getArticleTagId, Collectors.toList())
                ));

        Set<Integer> tagIds = relations.stream()
                .map(ArticleTagRelation::getArticleTagId)
                .collect(Collectors.toSet());

        List<ArticleTag> tagList = articleTagMapper.selectByIds(tagIds);
        Map<Integer, ArticleTag> tagMap = tagList.stream()
                .collect(Collectors.toMap(ArticleTag::getArticleTagId, t -> t));

        for (Article article : articleList) {
            List<Integer> tIds = articleToTagIds.getOrDefault(article.getArticleId(), Collections.emptyList());
            List<ArticleTag> ts = tIds.stream()
                    .filter(tagMap::containsKey)
                    .map(tagMap::get)
                    .collect(Collectors.toList());

            article.setTagIds(tIds);
            article.setArticleTagList(ts);
        }
    }

    /**
     * 根据【分类ID列表】查询文章ID
     */
    private List<Integer> getArticleIdsByCategoryIds(List<Integer> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ArticleCategoryRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ArticleCategoryRelation::getCategoryId, categoryIds);
        List<ArticleCategoryRelation> relations = articleCategoryRelationMapper.selectList(wrapper);

        return relations.stream()
                .map(ArticleCategoryRelation::getArticleId)
                .collect(Collectors.toList());
    }

    /**
     * 根据【标签ID】查询文章ID
     */
    private List<Integer> getArticleIdsByTagId(Integer articleTagId) {
        LambdaQueryWrapper<ArticleTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTagRelation::getArticleTagId, articleTagId);
        List<ArticleTagRelation> relations = articleTagRelationMapper.selectList(wrapper);

        return relations.stream()
                .map(ArticleTagRelation::getArticleId)
                .collect(Collectors.toList());
    }
}