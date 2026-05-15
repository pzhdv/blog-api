package cn.pzhdv.blog.controller;


import cn.pzhdv.blog.constant.RedisKey;
import cn.pzhdv.blog.entity.JobExperience;
import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultUtil;
import cn.pzhdv.blog.service.JobExperienceService;
import cn.pzhdv.blog.utils.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 工作经历表 前端控制器
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Api(tags = "工作经验模块")
@RestController
@RequestMapping("/jobExperience")
@RequiredArgsConstructor
public class JobExperienceController {

    private final JobExperienceService service;
    private final RedisUtils redisUtils;

    @ApiOperation(value = "查询列表", notes = "查询所有列表", httpMethod = "GET", produces = "application/json")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result<List<JobExperience>> list() {
        // 先从 Redis 中获取缓存数据
        List<JobExperience> list = redisUtils.get(RedisKey.JOB_EXPERIENCE_CACHE_KEY, new TypeReference<>() {
        });
        if (list == null) {
            list = service.queryJobExperienceList();
            redisUtils.set(RedisKey.JOB_EXPERIENCE_CACHE_KEY, list);
        }
        return ResultUtil.ok(list);
    }

}

