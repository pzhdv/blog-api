package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.BlogMission;
import cn.pzhdv.blog.exception.BusinessException;
import cn.pzhdv.blog.mapper.BlogMissionMapper;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.service.BlogMissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 博客使命表 服务实现类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Service
@RequiredArgsConstructor
public class BlogMissionServiceImpl extends ServiceImpl<BlogMissionMapper, BlogMission> implements BlogMissionService {

    private final BlogMissionMapper blogMissionMapper;

    @Override
    public BlogMission queryBlogMission() {
        LambdaQueryWrapper<BlogMission> queryWrapper = new LambdaQueryWrapper<>();
        // 性能优化：只查最多2条，判断是否重复即可
        queryWrapper.last("LIMIT 2");

        List<BlogMission> missionList = blogMissionMapper.selectList(queryWrapper);

        // 空数据返回null
        if (CollectionUtils.isEmpty(missionList)) {
            return null;
        }

        // 数据不唯一，抛出规范异常
        if (missionList.size() > 1) {
            throw new BusinessException(
                    ResultCode.DATA_DUPLICATE.getCode(),
                    ResultCode.DATA_DUPLICATE.message(missionList.size())
            );
        }

        return missionList.get(0);
    }
}