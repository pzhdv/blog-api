package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.Article;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.ArticleService;
import cn.pzhdv.blog.utils.DateUtils;
import cn.pzhdv.blog.utils.RedisUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 博客文章表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:08:59
 */
@Slf4j
@Api(tags = "Article模块")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService service;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "查询文章条总数", notes = "查询文章条总数,去除草稿文章", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "total", method = RequestMethod.GET)
    public Result queryArticleTotal() {
        // 先从 Redis 中获取缓存数据
        String redisKey = RedisKey.ARTICLE_TOTAL_KEY;
        Long articleTotal = redisUtils.get(redisKey, Long.class);

        if (articleTotal == null) {
            // 如果 Redis 中没有缓存数据，则查询数据库
            articleTotal = service.queryArticleTotal(true);
            // 将查询结果存入 Redis
            redisUtils.set(redisKey, articleTotal);
        }

        return ResultUtil.ok(articleTotal);
    }


    @ApiOperation(value = "查询文章发布时间列表", notes = "查询文章发布时间,去除草稿文章", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "publishDateList", method = RequestMethod.GET)
    public Result articlePublishDateList() {
        // 先从 Redis 中获取缓存数据
        String redisKey = RedisKey.ARTICLE_PUBLISH_DATE_LIST_KEY;
        List<Date> dates = redisUtils.get(redisKey, List.class);

        if (dates == null) {
            dates = service.queryArticlePublishDateList(true);
            redisUtils.set(redisKey, dates);
        }

        return ResultUtil.ok(dates);
    }

    @ApiOperation(value = "条件分页查询 首页", notes = "条件分页查询列表", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "publishDateStr", value = "发布日期 yyyy-MM-dd", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "articleTagId", value = "标签ID", paramType = "query", dataType = "Boolean", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
    })
    @RequestMapping(value = "mobileHomePageArticleList", method = RequestMethod.GET)
    public Result getMobileHomePageArticleList(
            @RequestParam(value = "publishDateStr", required = false) String publishDateStr,
            @RequestParam(value = "articleTagId", required = false) Integer articleTagId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Date publishDate = null;
        if (publishDateStr != null && !publishDateStr.isEmpty()) {
            try {
                publishDate = DateUtils.parseDate(publishDateStr);
            } catch (Exception e) {
                return ResultUtil.error(ResultCode.QUERY_FAIL.code(), "无效的日期格式. 请使用 'yyyy-MM-dd");
            }
        }
        // 构建缓存键
        String redisKey = RedisKey.ARTICLE_HOME_PAGE_LIST_KEY +
                ":publishDate=" + publishDateStr +
                ":articleTagId=" + articleTagId +
                ":pageNum=" + pageNum +
                ":pageSize=" + pageSize;

        // 先从 Redis 中获取缓存数据
        Page<Article> page = redisUtils.get(redisKey, Page.class);
        if (page == null) {
            // 如果 Redis 中没有缓存数据，则查询数据库
            page = service.queryMobileHomePageArticleList(publishDate, articleTagId, pageNum, pageSize, true);
            if (page != null) {
                // 将查询结果存入 Redis
                redisUtils.set(redisKey, page);
            }
        }
        return ResultUtil.ok(page);
    }

    @ApiOperation(value = "条件分页查询 分类页面", notes = "条件分页查询列表", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryIds", value = "分类Id列表", paramType = "query", dataType = "List<Integer>", dataTypeClass = List.class),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "query", dataType = "Integer", dataTypeClass = Integer.class),
    })
    @RequestMapping(value = "mobileCategoryPageArticleList", method = RequestMethod.GET)
    public Result getMobileCategoryPageArticleList(
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 构建缓存键
        String redisKey = RedisKey.ARTICLE_CATEGORY_PAGE_LIST_KEY +
                ":categoryIds=" + (categoryIds != null ? categoryIds.toString() : "null") +
                ":pageNum=" + pageNum +
                ":pageSize=" + pageSize;

        // 先从 Redis 中获取缓存数据
        Page<Article> page = redisUtils.get(redisKey, Page.class);
        if (page == null) {
            // 如果 Redis 中没有缓存数据，则查询数据库
            page = service.queryMobileCategoryPageArticleList(categoryIds, pageNum, pageSize, true);
            if (page != null) {
                // 将查询结果存入 Redis
                redisUtils.set(redisKey, page);
            }
        }
        return ResultUtil.ok(page);
    }


    @ApiOperation(value = "根据ID查询文章详情", notes = "根据ID查询文章详情", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章Id", required = true, dataType = "Integer", dataTypeClass = Integer.class)
    })
    @RequestMapping(value = "articleDetailById", method = RequestMethod.GET)
    public Result queryArticleDetailById(@RequestParam(value = "articleId", required = true) Integer articleId) {
        // 先从 Redis 中获取缓存数据
        String redisKey = RedisKey.ARTICLE_DETAIL_KEY + articleId;
        Article article = redisUtils.get(redisKey, Article.class);

        if (article == null) {
            article = service.queryArticleById(articleId);
            redisUtils.set(redisKey, article);
        }

        return ResultUtil.ok(article);
    }

}

