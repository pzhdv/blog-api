package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.ArticleTag;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.ArticleTagService;
import cn.pzhdv.blog.utils.RedisUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文章标签表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Api(tags = "ArticleTag模块")
@RestController
@RequestMapping("/articleTag")
public class ArticleTagController {

    @Autowired
    private ArticleTagService service;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "查询列表", notes = "查询所有列表", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result list() {
        // 先从 Redis 中获取缓存数据
        List<ArticleTag> list = redisUtils.get(RedisKey.ARTICLE_TAG_CATCH_KEY, List.class);
        if (list == null) {
            list = service.list();
            redisUtils.set(RedisKey.ARTICLE_TAG_CATCH_KEY, list);
        }
        return ResultUtil.ok(list);
    }

}

