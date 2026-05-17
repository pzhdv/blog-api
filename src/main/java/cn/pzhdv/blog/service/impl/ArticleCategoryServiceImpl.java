package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.ArticleCategory;
import cn.pzhdv.blog.entity.ArticleCategoryRelation;
import cn.pzhdv.blog.mapper.ArticleCategoryMapper;
import cn.pzhdv.blog.mapper.ArticleCategoryRelationMapper;
import cn.pzhdv.blog.service.ArticleCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategory> implements ArticleCategoryService {

    private final ArticleCategoryMapper categoryMapper;
    private final ArticleCategoryRelationMapper relationMapper;

    @Override
    public List<ArticleCategory> queryCategoryTreeWithCount(Integer rootId) {
        // 1. 一次性查询全部分类
        List<ArticleCategory> allCategories = categoryMapper.selectList(null);
        if (CollectionUtils.isEmpty(allCategories)) {
            return Collections.emptyList();
        }

        // 2. 数据库层面分组统计（性能最优）
        LambdaQueryWrapper<ArticleCategoryRelation> queryWrapper = new QueryWrapper<ArticleCategoryRelation>()
                .select("category_id", "COUNT(*) AS total")
                .lambda()
                .groupBy(ArticleCategoryRelation::getCategoryId);
        List<Map<String, Object>> countMaps = relationMapper.selectMaps(queryWrapper);

        // 3. 标准方式转换为 Map<Integer, Long>
        Map<Integer, Long> selfArticleCountMap = new HashMap<>();
        for (Map<String, Object> map : countMaps) {
            Integer categoryId = Integer.valueOf(map.get("category_id").toString());
            Long total = Long.valueOf(map.get("total").toString());
            selfArticleCountMap.put(categoryId, total);
        }

        // 4. 按 parentId 分组
        Map<Integer, List<ArticleCategory>> parentGroupMap = allCategories.stream()
                .collect(Collectors.groupingBy(ArticleCategory::getParentId));

        // 5. 构建树 + 自下而上统计总数
        return buildTreeAndCalculateTotal(rootId, parentGroupMap, selfArticleCountMap);
    }

    /**
     * 递归构建分类树，并计算：自身文章数 + 所有子分类文章总数
     */
    private List<ArticleCategory> buildTreeAndCalculateTotal(
            Integer parentId,
            Map<Integer, List<ArticleCategory>> parentGroupMap,
            Map<Integer, Long> selfArticleCountMap) {

        List<ArticleCategory> children = parentGroupMap.getOrDefault(parentId, Collections.emptyList());

        for (ArticleCategory category : children) {
            // 递归子节点
            List<ArticleCategory> subChildren = buildTreeAndCalculateTotal(
                    category.getCategoryId(), parentGroupMap, selfArticleCountMap
            );
            category.setChildren(subChildren);

            // 自身文章数
            long selfCount = selfArticleCountMap.getOrDefault(category.getCategoryId(), 0L);

            // 所有子分类的总文章数
            long childrenTotalCount = 0;
            for (ArticleCategory sub : subChildren) {
                childrenTotalCount += sub.getArticleTotal();
            }

            // 最终总数
            category.setArticleTotal(selfCount + childrenTotalCount);
        }
        return children;
    }
}