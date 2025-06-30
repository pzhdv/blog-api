package cn.pzhdv.blog.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章分类表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("article_category")
@ApiModel(value = "ArticleCategory对象", description = "文章分类表")
public class ArticleCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分类ID")
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    @TableField("category_name")
    private String categoryName;

    @ApiModelProperty("父分类ID")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty("字体图标")
    @TableField("icon_class")
    private String iconClass;

    @ApiModelProperty("分类下的文章总数")
    @TableField(exist = false)
    private Long articleTotal;

    @ApiModelProperty("子分类")
    @TableField(exist = false)
    private List<ArticleCategory> children;

}
