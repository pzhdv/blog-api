package cn.pzhdv.blog.exception;

/**
 * @author PanZonghui
 * @since 2025-04-24 13:10:16
 * @Version: 1.0
 * @Description 业务异常处理
 */
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
