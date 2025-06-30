package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.BlogMission;
import cn.pzhdv.blog.exception.BusinessException;
import cn.pzhdv.blog.mapper.BlogMissionMapper;
import cn.pzhdv.blog.service.BlogMissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class BlogMissionServiceImpl extends ServiceImpl<BlogMissionMapper, BlogMission> implements BlogMissionService {

    @Autowired
    private BlogMissionMapper mapper;

    @Override
    public BlogMission queryBlogMission() {
        QueryWrapper<BlogMission> queryWrapper = new QueryWrapper();
        List<BlogMission> blogMissionList = mapper.selectList(queryWrapper);
        if (blogMissionList != null && blogMissionList.size() > 0) {
            if (blogMissionList.size() > 1) {
                throw new BusinessException(9999, "查询博客信息表失败,数据不唯一");
            } else {
                return blogMissionList.get(0);
            }
        }
        return null;
    }
}
