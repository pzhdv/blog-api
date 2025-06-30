package cn.pzhdv.blog.service;

import cn.pzhdv.blog.entity.ArticleCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文章分类表 服务类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
public interface ArticleCategoryService extends IService<ArticleCategory> {

    /**
     * 递归查询分类及其所有子分类
     * @param parentId 父分类ID
     * @return 分类树
     */
    List<ArticleCategory> listAllChildren(Integer parentId);

    /**
     * 查询分类及其所有子分类，并设置每个分类下的文章总数
     * @param parentId 父分类ID
     * @return 分类树
     */
    List<ArticleCategory> queryArticleCategoryListAndArticleTotal(Integer parentId);
}
