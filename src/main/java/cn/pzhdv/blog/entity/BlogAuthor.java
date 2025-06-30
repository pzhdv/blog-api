package cn.pzhdv.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户个人信息表
 * </p>
 *
 * @author PanZonghui
 * @since 2025-06-25 21:03:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("blog_author")
@ApiModel(value = "BlogAuthor对象", description = "用户个人信息表")
public class BlogAuthor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @ApiModelProperty("姓名")
    @TableField("full_name")
    private String fullName;

    @ApiModelProperty("用户头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty("职位")
    @TableField("position")
    private String position;

    @ApiModelProperty("个人简介")
    @TableField("self_introduction")
    private String selfIntroduction;

    @ApiModelProperty("个人邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("个人站点")
    @TableField("website")
    private String website;

    @ApiModelProperty("github连接")
    @TableField("github")
    private String github;

    @ApiModelProperty("个人联系电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("用户昵称")
    @TableField("user_nick")
    private String userNick;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("生日")
    @TableField("birthday")
    private Date birthday;

    @ApiModelProperty("学历")
    @TableField("education_level")
    private String educationLevel;

    @ApiModelProperty("学校名称")
    @TableField("school_name")
    private String schoolName;

}
