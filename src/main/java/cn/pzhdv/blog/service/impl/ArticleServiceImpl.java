package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.*;
import cn.pzhdv.blog.mapper.*;
import cn.pzhdv.blog.service.ArticleService;
import cn.pzhdv.blog.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客文章表 服务实现类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:08:59
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagRelationMapper articleTagRelationMapper;
    @Autowired
    private ArticleCategoryRelationMapper articleCategoryRelationMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Transactional
    @Override
    public boolean deleteArticleById(Integer articleId) {
        articleMapper.deleteById(articleId);
        deleteArticleRelations(articleId);// 删除关系
        return true;
    }

    @Override
    public Long queryArticleTotal(Boolean publishState) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publish_state", publishState);
        return articleMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Date> queryArticlePublishDateList(Boolean publishState) {
        // 指定查询的字段
        String[] columns = {"update_time"};
        // 创建查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(columns);

        queryWrapper.eq("publish_state", publishState);

        // 执行查询，获取文章列表
        List<Article> articles = articleMapper.selectList(queryWrapper);

        // 提取 update_time 字段的值，并将其转换为 List<Date>
        List<Date> publishDateList = articles.stream()
                .map(article -> article.getUpdateTime()) // 假设 Article 类中有一个 getUpdateTime 方法
                .collect(Collectors.toList());

        return publishDateList;
    }


    @Transactional
    @Override
    public boolean updateArticle(Article article) {
        Integer articleId = article.getArticleId();

        articleMapper.updateById(article);

        deleteArticleRelations(articleId);// 删除关系

        List<Integer> categoryIds = article.getCategoryIds();
        List<Integer> tagIds = article.getTagIds();
        saveArticleRelations(articleId, categoryIds, tagIds);//重新建立关系

        return true;
    }

    @Transactional
    @Override
    public boolean saveArticle(Article article) {
        articleMapper.insert(article);
        List<Integer> categoryIds = article.getCategoryIds();
        List<Integer> tagIds = article.getTagIds();
        Integer articleId = article.getArticleId();
        saveArticleRelations(articleId, categoryIds, tagIds);//建立关系
        return true;
    }

    @Override
    public List<Article> queryArticleList() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        // 要查询的字段 不全部查
        String[] columns = {"article_id", "title", "image", "excerpt", "publish_state", "recommend_weight", "update_time"};
        queryWrapper.select(columns);
        queryWrapper.orderByDesc("recommend_weight", "update_time");
        List<Article> articleList = articleMapper.selectList(queryWrapper); // 1、查询文章列表
        for (Article article : articleList) {
            setArticleCategories(article);
            setArticleTags(article);
        }
        return articleList;
    }

    @Override
    public Article queryArticleById(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return null;
        }
        setArticleCategories(article);
        setArticleTags(article);
        return article;
    }

    @Override
    public Page<Article> queryArticleListByConditionPage(String title, Boolean publishState, Date startDate, Date endDate, String excerptKeyWorld, Integer recommendWeight, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Article> page = new Page<>(pageNum, pageSize);

        // 创建 QueryWrapper
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 添加排序条件
        queryWrapper.orderByDesc("recommend_weight").orderByDesc("update_time");

        // 添加查询条件
        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (publishState != null) {
            queryWrapper.eq("publish_state", publishState);
        }
        if (startDate != null) {
            queryWrapper.ge("update_time", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("update_time", endDate);
        }
        if (excerptKeyWorld != null && !excerptKeyWorld.isEmpty()) {
            queryWrapper.like("excerpt", excerptKeyWorld);
        }
        if (recommendWeight != null) {
            queryWrapper.eq("recommend_weight", recommendWeight);
        }


        // 执行分页查询
        Page<Article> resultPage = articleMapper.selectPage(page, queryWrapper);

        // 封装分类和标签设置逻辑
        resultPage.convert(article -> {
            setArticleCategories(article);
            setArticleTags(article);
            return article;
        });

        // 返回分页结果
        return resultPage;
    }

    @Override
    public Page<Article> queryMobileHomePageArticleList(Date publishDate, Integer articleTagId, Integer pageNum, Integer pageSize, Boolean publishState) {
        // 创建分页对象
        Page<Article> page = new Page<>(pageNum, pageSize);

        // 创建 QueryWrapper
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 添加排序条件
        queryWrapper.orderByDesc("recommend_weight").orderByDesc("update_time").orderByDesc("article_id");

        // 如果提供了发布日期，则添加发布日期的过滤条件
        if (publishDate != null) {
            // 计算当天的开始时间和结束时间
            Date startOfDay = DateUtils.startOfDay(publishDate);
            Date endOfDay = DateUtils.endOfDay(publishDate);
            queryWrapper.ge("update_time", startOfDay);
            queryWrapper.le("update_time", endOfDay);
        }

        if (publishState != null) {
            queryWrapper.eq("publish_state", publishState);
        }

        // 如果提供了文章标签 ID，则添加标签过滤条件
        if (articleTagId != null) {
            List<Integer> articleIdList = getArticleIdListByTagId(articleTagId);
            if (articleIdList != null && !articleIdList.isEmpty()) {
                queryWrapper.in("article_id", articleIdList);
            }
        }

        // 执行分页查询
        Page<Article> resultPage = articleMapper.selectPage(page, queryWrapper);

        // 封装分类和标签设置逻辑
        resultPage.convert(article -> {
            setArticleCategories(article);
            setArticleTags(article);
            return article;
        });

        // 返回分页结果
        return resultPage;
    }

    @Override
    public Page<Article> queryMobileCategoryPageArticleList(List<Integer> categoryIds, Integer pageNum, Integer pageSize, Boolean publishState) {
        // 创建分页对象
        Page<Article> page = new Page<>(pageNum, pageSize);

        // 创建 QueryWrapper
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 添加排序条件
        queryWrapper.orderByDesc("article_id");

        if (publishState != null) {
            queryWrapper.eq("publish_state", publishState);
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Integer> categoryIdList = getArticleIdListByCategoryIds(categoryIds);
            if (categoryIdList == null || categoryIdList.isEmpty()) {
                return page; // 分类下无文章 返回空分页
            }
            queryWrapper.in("article_id", categoryIdList);
        }

        // 执行分页查询
        Page<Article> resultPage = articleMapper.selectPage(page, queryWrapper);

        // 封装分类和标签设置逻辑
        resultPage.convert(article -> {
            setArticleCategories(article);
            setArticleTags(article);
            return article;
        });

        // 返回分页结果
        return resultPage;
    }

    /**
     * 根据文章id建立 【标签、分类】 与文章的关系
     *
     * @param articleId
     * @param categoryIds
     * @param tagIds
     */
    private void saveArticleRelations(Integer articleId, List<Integer> categoryIds, List<Integer> tagIds) {
        if (categoryIds != null) {
            for (Integer categoryId : categoryIds) {
                ArticleCategoryRelation relation = new ArticleCategoryRelation();
                relation.setArticleId(articleId);
                relation.setCategoryId(categoryId);
                articleCategoryRelationMapper.insert(relation);
            }
        }
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                ArticleTagRelation relation = new ArticleTagRelation();
                relation.setArticleId(articleId);
                relation.setArticleTagId(tagId);
                articleTagRelationMapper.insert(relation);
            }
        }
    }

    /**
     * 根据文章id删除 【标签、分类】 与文章的关系
     *
     * @param articleId
     */
    private void deleteArticleRelations(Integer articleId) {
        // 删除标签ArticleTag关系
        QueryWrapper<ArticleTagRelation> tagRelationQueryWrapper = new QueryWrapper<>();
        tagRelationQueryWrapper.eq("article_id", articleId);
        articleTagRelationMapper.delete(tagRelationQueryWrapper);

        // 删除分类ArticleCategory关系
        QueryWrapper<ArticleCategoryRelation> categoryRelationQueryWrapper = new QueryWrapper<>();
        categoryRelationQueryWrapper.eq("article_id", articleId);
        articleCategoryRelationMapper.delete(categoryRelationQueryWrapper);
    }

    /**
     * 设置文章分类
     *
     * @param article
     */
    private void setArticleCategories(Article article) {
        if (article == null) {
            return;
        }
        Integer articleId = article.getArticleId();
        QueryWrapper<ArticleCategoryRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        List<ArticleCategoryRelation> relations = articleCategoryRelationMapper.selectList(queryWrapper);
        if (relations == null || relations.isEmpty()) {
            article.setCategoryIds(new ArrayList<>());
            article.setArticleCategoryList(new ArrayList<>());
            return;
        }
        List<Integer> categoryIds = relations.stream()
                .map(ArticleCategoryRelation::getCategoryId)
                .collect(Collectors.toList());
        article.setCategoryIds(categoryIds);

        QueryWrapper<ArticleCategory> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.in("category_id", categoryIds);
        List<ArticleCategory> categoryList = articleCategoryMapper.selectList(categoryWrapper);
        article.setArticleCategoryList(categoryList);
    }

    /**
     * 根据分类 ID列表 查询文章 ID 列表
     *
     * @param categoryIds 分类id列表
     * @return
     */
    private List<Integer> getArticleIdListByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 创建 QueryWrapper
        QueryWrapper<ArticleCategoryRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("category_id", categoryIds);

        // 查询关联表，获取所有与指定标签 ID 相关联的记录
        List<ArticleCategoryRelation> relations = articleCategoryRelationMapper.selectList(queryWrapper);

        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取文章 ID 列表
        List<Integer> articleIdList = new ArrayList<>();
        for (ArticleCategoryRelation relation : relations) {
            articleIdList.add(relation.getArticleId());
        }

        return articleIdList;
    }

    /**
     * 根据标签 ID 查询文章 ID 列表
     *
     * @param articleTagId
     * @return
     */
    private List<Integer> getArticleIdListByTagId(Integer articleTagId) {
        // 创建 QueryWrapper
        QueryWrapper<ArticleTagRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_tag_id", articleTagId);

        // 查询关联表，获取所有与指定标签 ID 相关联的记录
        List<ArticleTagRelation> relations = articleTagRelationMapper.selectList(queryWrapper);

        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取文章 ID 列表
        List<Integer> articleIdList = new ArrayList<>();
        for (ArticleTagRelation relation : relations) {
            articleIdList.add(relation.getArticleId());
        }

        return articleIdList;
    }

    /**
     * 设置文章标签
     *
     * @param article
     */
    private void setArticleTags(Article article) {
        if (article == null) {
            return;
        }
        Integer articleId = article.getArticleId();
        QueryWrapper<ArticleTagRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        List<ArticleTagRelation> relations = articleTagRelationMapper.selectList(queryWrapper);
        if (relations == null || relations.isEmpty()) {
            article.setTagIds(new ArrayList<>());
            article.setArticleTagList(new ArrayList<>());
            return;
        }
        List<Integer> tagIds = relations.stream()
                .map(ArticleTagRelation::getArticleTagId)
                .collect(Collectors.toList());
        article.setTagIds(tagIds);

        QueryWrapper<ArticleTag> tagWrapper = new QueryWrapper<>();
        tagWrapper.in("article_tag_id", tagIds);
        List<ArticleTag> tagList = articleTagMapper.selectList(tagWrapper);
        article.setArticleTagList(tagList);
    }
}
