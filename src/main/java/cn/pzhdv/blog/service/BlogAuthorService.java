package cn.pzhdv.blog.service;

import cn.pzhdv.blog.entity.BlogAuthor;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户个人信息表 服务类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
public interface BlogAuthorService extends IService<BlogAuthor> {

    BlogAuthor queryBlogAuthor();
}
