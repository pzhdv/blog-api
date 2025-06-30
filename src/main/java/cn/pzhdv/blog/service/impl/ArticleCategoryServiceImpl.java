package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.ArticleCategory;
import cn.pzhdv.blog.entity.ArticleCategoryRelation;
import cn.pzhdv.blog.mapper.ArticleCategoryMapper;
import cn.pzhdv.blog.mapper.ArticleCategoryRelationMapper;
import cn.pzhdv.blog.service.ArticleCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章分类表 服务实现类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategory> implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelationMapper relationMapper;

    /**
     * 查询直接子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表，如果为空则返回空列表
     */
    public List<ArticleCategory> getDirectChildren(Integer parentId) {
        QueryWrapper<ArticleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId)
                .orderByAsc("category_id");
        List<ArticleCategory> list = categoryMapper.selectList(queryWrapper);
        return list != null ? list : Collections.emptyList();
    }


    /**
     * 递归收集所有分类的ID
     *
     * @param parentId 父分类ID
     * @return 分类ID列表
     */
    private List<Integer> collectCategoryIds(Integer parentId) {
        List<ArticleCategory> directChildren = getDirectChildren(parentId);
        List<Integer> categoryIds = new ArrayList<>();
        for (ArticleCategory child : directChildren) {
            categoryIds.add(child.getCategoryId());
            categoryIds.addAll(collectCategoryIds(child.getCategoryId()));
        }
        return categoryIds;
    }

    /**
     * 批量查询分类下的文章总数
     *
     * @param categoryIds 分类ID列表
     * @return 分类ID到文章总数的映射
     */
    private Map<Integer, Long> getArticleTotalMap(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        QueryWrapper<ArticleCategoryRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("category_id", categoryIds);
        List<ArticleCategoryRelation> relations = relationMapper.selectList(queryWrapper);

        // 使用流式操作构建分类ID到文章总数的映射
        return relations.stream()
                .collect(Collectors.groupingBy(
                        ArticleCategoryRelation::getCategoryId,
                        Collectors.counting()
                ));
    }


    /**
     * 递归为分类设置文章总数
     *
     * @param categories 分类列表
     * @param articleTotalMap 分类ID到文章总数的映射
     */
    private void setArticleTotalForCategories(List<ArticleCategory> categories, Map<Integer, Long> articleTotalMap) {
        for (ArticleCategory category : categories) {
            category.setArticleTotal(articleTotalMap.getOrDefault(category.getCategoryId(), 0L));
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                setArticleTotalForCategories(category.getChildren(), articleTotalMap);
            }
        }
    }

    /**
     * 查询分类及其所有子分类，并设置每个分类下的文章总数
     *
     * @param parentId 父分类ID
     * @return 分类树
     */
    @Override
    public List<ArticleCategory> queryArticleCategoryListAndArticleTotal(Integer parentId) {
        // 递归查询所有子分类
        List<ArticleCategory> allChildren = listAllChildren(parentId);

        // 递归收集所有分类的ID
        List<Integer> categoryIds = collectCategoryIds(parentId);

        // 批量查询所有分类的文章总数
        Map<Integer, Long> articleTotalMap = getArticleTotalMap(categoryIds);

        // 递归为分类设置文章总数
        setArticleTotalForCategories(allChildren, articleTotalMap);

        return allChildren;
    }


    /**
     * 递归查询分类及其所有子分类
     * @param parentId 父分类ID
     * @return 分类树
     */
    @Override
    public List<ArticleCategory> listAllChildren(Integer parentId) {
        List<ArticleCategory> directChildren = getDirectChildren(parentId);
        for (ArticleCategory child : directChildren) {
            // 递归查询当前子分类的所有子分类，并为它们设置文章总数
            List<ArticleCategory> subChildren = queryArticleCategoryListAndArticleTotal(child.getCategoryId());
            child.setChildren(subChildren);
        }
        return directChildren;
    }

}