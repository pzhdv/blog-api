package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.BlogAuthor;
import cn.pzhdv.blog.exception.BusinessException;
import cn.pzhdv.blog.mapper.BlogAuthorMapper;
import cn.pzhdv.blog.service.BlogAuthorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户个人信息表 服务实现类
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Service
public class BlogAuthorServiceImpl extends ServiceImpl<BlogAuthorMapper, BlogAuthor> implements BlogAuthorService {

    @Autowired
    private BlogAuthorMapper mapper;

    @Override
    public BlogAuthor queryBlogAuthor() {
        QueryWrapper<BlogAuthor> queryWrapper = new QueryWrapper();
        List<BlogAuthor> blogAuthorList = mapper.selectList(queryWrapper);
        if (blogAuthorList != null && blogAuthorList.size() > 0) {
            if (blogAuthorList.size() > 1) {
                throw new BusinessException(9999, "查询用户个人信息表失败,数据不唯一");
            } else {
                return blogAuthorList.get(0);
            }
        }
        return null;
    }
}
