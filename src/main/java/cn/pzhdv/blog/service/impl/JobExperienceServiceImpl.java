package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.JobExperience;
import cn.pzhdv.blog.mapper.JobExperienceMapper;
import cn.pzhdv.blog.service.JobExperienceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工作经历表 服务实现类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Service
public class JobExperienceServiceImpl extends ServiceImpl<JobExperienceMapper, JobExperience> implements JobExperienceService {

    @Autowired
    private JobExperienceMapper mapper;

    @Override
    public List<JobExperience> queryJobExperienceList() {
        QueryWrapper<JobExperience> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        return mapper.selectList(wrapper);
    }
}
