package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.ArticleTag;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.ArticleTagService;
import cn.pzhdv.blog.utils.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@Api(tags = "文章标签模块")
@RestController
@RequestMapping("/articleTag")
@RequiredArgsConstructor
public class ArticleTagController {

    private final ArticleTagService service;
    private final RedisUtils redisUtils;

    @ApiOperation(value = "查询列表", notes = "查询所有列表", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result<List<ArticleTag>> list() {
        // 先从 Redis 中获取缓存数据
        List<ArticleTag> list = redisUtils.get(RedisKey.ARTICLE_TAG_CACHE_KEY, new TypeReference<>() {
        });
        if (list == null) {
            list = service.list();
            redisUtils.set(RedisKey.ARTICLE_TAG_CACHE_KEY, list);
        }
        return ResultUtil.ok(list);
    }

}

