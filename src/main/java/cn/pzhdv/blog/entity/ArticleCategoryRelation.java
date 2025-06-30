package cn.pzhdv.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章-分类关联表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("article_category_relation")
@ApiModel(value = "ArticleCategoryRelation对象", description = "文章-分类关联表")
public class ArticleCategoryRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("文章ID")
    @TableField("article_id")
    private Integer articleId;

    @ApiModelProperty("分类ID")
    @TableField("category_id")
    private Integer categoryId;


}
