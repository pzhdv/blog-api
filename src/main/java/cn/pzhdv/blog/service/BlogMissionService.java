package cn.pzhdv.blog.service;

import cn.pzhdv.blog.entity.BlogMission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 博客使命表 服务类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
public interface BlogMissionService extends IService<BlogMission> {

    BlogMission queryBlogMission();
}
