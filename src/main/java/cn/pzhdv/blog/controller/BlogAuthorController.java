package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.BlogAuthor;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.BlogAuthorService;
import cn.pzhdv.blog.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户个人信息表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Api(tags = "博客作者模块")
@RestController
@RequestMapping("/blogAuthor")
@RequiredArgsConstructor
public class BlogAuthorController {


    private final BlogAuthorService service;

    private final RedisUtils redisUtils;

    @ApiOperation(value = "查询", notes = "查询作者用户信息", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "currentUserInfo", method = RequestMethod.GET)
    public Result<BlogAuthor> currentUserInfo() {
        BlogAuthor blogAuthor = redisUtils.get(RedisKey.BLOG_AUTHOR_CACHE_KEY, BlogAuthor.class);
        if (blogAuthor == null) {
            blogAuthor = service.queryBlogAuthor();
            redisUtils.set(RedisKey.BLOG_AUTHOR_CACHE_KEY, blogAuthor);
        }
        return ResultUtil.ok(blogAuthor);
    }
}

