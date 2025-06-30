package cn.pzhdv.blog.service;

import cn.pzhdv.blog.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 博客文章表 服务类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:08:59
 */
public interface ArticleService extends IService<Article> {

    List<Article> queryArticleList();

    Page<Article> queryArticleListByConditionPage(String title, Boolean publishState, Date startDate, Date endDate, String excerptKeyWorld, Integer recommendWeight, Integer pageNum, Integer pageSize);

    Article queryArticleById(Integer articleId);

    boolean saveArticle(Article article);

    boolean updateArticle(Article article);

    boolean deleteArticleById(Integer articleId);

    Long queryArticleTotal(Boolean publishState);

    List<Date> queryArticlePublishDateList(Boolean publishState);

    Page<Article> queryMobileHomePageArticleList(Date publishDate, Integer articleTagId, Integer pageNum, Integer pageSize, Boolean publishState);

    Page<Article> queryMobileCategoryPageArticleList(List<Integer> categoryIds, Integer pageNum, Integer pageSize, Boolean publishState);
}
