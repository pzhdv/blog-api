package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.BlogMission;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.BlogMissionService;
import cn.pzhdv.blog.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 博客使命表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Api(tags = "BlogMission模块")
@RestController
@RequestMapping("/blogMission")
public class BlogMissionController {

    @Autowired
    private BlogMissionService service;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "查询", notes = "查询博客使命实体对象信息", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "blogMissionInfo", method = RequestMethod.GET)
    public Result list() {
        // 先从 Redis 中获取缓存数据
        BlogMission blogMission = redisUtils.get(RedisKey.BLOG_MISSION_CATCH_KEY, BlogMission.class);
        if (blogMission == null) {
            blogMission = service.queryBlogMission();
            redisUtils.set(RedisKey.BLOG_MISSION_CATCH_KEY, blogMission);
        }
        return ResultUtil.ok(blogMission);

    }
}

