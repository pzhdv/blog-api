package cn.pzhdv.blog.result;

/**
 * @author PanZonghui
 * @since 2025-06-25 13:10:16
 * @Version: 1.0
 * @Description 返回结果工具类
 */
public class ResultUtil {
    /**
     * 封装请求成功返回结果
     *
     * @param data
     * @author panzonghui
     * @date 2025年06月25日 15:43
     */
    public static Result ok(Object data) {
        return new Result(ResultCode.SUCCESS.code(), ResultCode.SUCCESS.message(), data);
    }

    /**
     * 封装请求异常返回结果
     *
     * @param code
     * @param message
     * @author panzonghui
     * @date 2025年06月25日 15:43
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<T>(code, message, null);
    }
}
