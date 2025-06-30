package cn.pzhdv.blog.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PanZonghui
 * @since 2025-06-25 13:10:16
 * @Version: 1.0
 * @Description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "响应结果", description = "")
public class Result<T> {
    /**
     * 返回结果状态
     */
    @ApiModelProperty(value = "返回结果状态码")
    private Integer code;
    /**
     * 返回结果提示信息
     */
    @ApiModelProperty(value = "返回结果提示信息")
    private String message;
    /**
     * 返回结果数据
     */
    @ApiModelProperty(value = "返回结果数据")
    private T data;
}
