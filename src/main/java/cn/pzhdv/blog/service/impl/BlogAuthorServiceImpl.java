package cn.pzhdv.blog.service.impl;

import cn.pzhdv.blog.entity.BlogAuthor;
import cn.pzhdv.blog.exception.BusinessException;
import cn.pzhdv.blog.mapper.BlogAuthorMapper;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.service.BlogAuthorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
@RequiredArgsConstructor
public class BlogAuthorServiceImpl extends ServiceImpl<BlogAuthorMapper, BlogAuthor> implements BlogAuthorService {

    private final BlogAuthorMapper blogAuthorMapper;

    @Override
    public BlogAuthor queryBlogAuthor() {
        // 使用 LambdaQueryWrapper 避免硬编码字段名，类型安全
        LambdaQueryWrapper<BlogAuthor> queryWrapper = new LambdaQueryWrapper<>();
        // 只查询一条即可，无需查询全部，提升性能
        queryWrapper.last("LIMIT 2");

        List<BlogAuthor> authorList = blogAuthorMapper.selectList(queryWrapper);

        // 集合判空使用 Spring 工具类，更规范
        if (CollectionUtils.isEmpty(authorList)) {
            return null;
        }

        // 数据不唯一，抛出业务异常
        if (authorList.size() > 1) {
            throw new BusinessException(
                    ResultCode.DATA_DUPLICATE.getCode(),
                    ResultCode.DATA_DUPLICATE.message(authorList.size())
            );
        }

        // 返回唯一数据
        return authorList.get(0);
    }
}
