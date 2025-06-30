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
 * 博客使命表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("blog_mission")
@ApiModel(value = "BlogMission对象", description = "博客使命表")
public class BlogMission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("博客使命Id")
    @TableId(value = "mission_id", type = IdType.AUTO)
    private Long missionId;

    @ApiModelProperty("使命标题")
    @TableField("mission_title")
    private String missionTitle;

    @ApiModelProperty("使命描述")
    @TableField("mission_description")
    private String missionDescription;

    @ApiModelProperty("具体使命要点")
    @TableField("mission_point_list_str")
    private String missionPointListStr;


}
