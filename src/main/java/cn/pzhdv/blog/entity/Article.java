package cn.pzhdv.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 博客文章表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:08:59
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("article")
@ApiModel(value = "Article对象", description = "博客文章表")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文章ID")
    @TableId(value = "article_id", type = IdType.AUTO)
    private Integer articleId;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("Markdown内容")
    @TableField("markdown")
    private String markdown;

    @ApiModelProperty("封面图路径/URL")
    @TableField("image")
    private String image;

    @ApiModelProperty("摘要")
    @TableField("excerpt")
    private String excerpt;

    @ApiModelProperty("发布状态")
    @TableField("publish_state")
    private Boolean publishState;

    @ApiModelProperty("推荐权重")
    @TableField("recommend_weight")
    private Integer recommendWeight;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty("分类ids")
    @TableField(exist = false)
    private List<Integer> categoryIds;

    @ApiModelProperty("标签ids")
    @TableField(exist = false)
    private List<Integer> tagIds;

    @ApiModelProperty("分类列表")
    @TableField(exist = false)
    private List<ArticleCategory> articleCategoryList;

    @ApiModelProperty("标签列表")
    @TableField(exist = false)
    private List<ArticleTag> articleTagList;

}
