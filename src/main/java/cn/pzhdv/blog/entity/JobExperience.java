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
 * 工作经历表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("job_experience")
@ApiModel(value = "JobExperience对象", description = "工作经历表")
public class JobExperience implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("经历Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("经历或成就的标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("所属组织")
    @TableField("organization")
    private String organization;

    @ApiModelProperty("时间范围")
    @TableField("time_range")
    private String timeRange;

    @ApiModelProperty("标题icon图标类")
    @TableField("title_icon")
    private String titleIcon;

    @ApiModelProperty("具体的成就列表字符串")
    @TableField("achievement_list_str")
    private String achievementListStr;

}
