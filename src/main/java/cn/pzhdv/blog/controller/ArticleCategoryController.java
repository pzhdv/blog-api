package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.ArticleCategory;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.ArticleCategoryService;
import cn.pzhdv.blog.utils.RedisUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文章分类表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Api(tags = "ArticleCategory模块")
@RestController
@RequestMapping("/articleCategory")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService service;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "查询分类条总数", notes = "查询分类条总数", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "total", method = RequestMethod.GET)
    public Result articleCategoryTotal() {
        // 先从 Redis 中获取缓存数据
        String redisKey = RedisKey.ARTICLE_CATEGORY_TOTAL_KEY;
        Long articleCategoryTotal = redisUtils.get(redisKey, Long.class);
        if (articleCategoryTotal == null) {
            // 如果 Redis 中没有缓存数据，则查询数据库
            articleCategoryTotal = service.count();
            // 将查询结果存入 Redis
            redisUtils.set(redisKey, articleCategoryTotal);
        }
        return ResultUtil.ok(articleCategoryTotal);
    }

    @ApiOperation(value = "查询分类列表", notes = "查询分类列表、包含该分类下的文章总条数", httpMethod = "GET", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "分类根节点ID", defaultValue = "0", required = false, dataType = "Integer", dataTypeClass = Integer.class)
    })
    @RequestMapping(value = "categoryListWithArticleCount", method = RequestMethod.GET)
    public Result getCategoryListWithArticleCount(@RequestParam(value = "parentId", defaultValue = "0", required = false) Integer parentId) {
        // 先从 Redis 中获取缓存数据
        String redisKey = RedisKey.ARTICLE_CATEGORY_LIST_KEY + ":parentId=" + parentId;
        List<ArticleCategory> list = redisUtils.get(redisKey, List.class);
        if (list == null) {
            list = service.queryArticleCategoryListAndArticleTotal(parentId);
            redisUtils.set(redisKey, list);
        }
        return ResultUtil.ok(list);
    }

}

