package cn.pzhdv.blog.mapper;

import cn.pzhdv.blog.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 博客文章表 Mapper 接口
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:08:59
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
