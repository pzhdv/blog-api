package cn.pzhdv.blog.result;

/**
 * @author PanZonghui
 * @since 2025-06-25 13:10:16
 * @Version: 1.0
 * @Description 返回状态码
 */
public enum ResultCode {
    // 基本
    SUCCESS(200, "请求成功"),

    /* 客户端错误类 (4xx) */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未经授权的访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "不支持的请求方法"),
    UNPROCESSABLE_ENTITY(422, "请求参数验证失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    /* 服务端错误类 (5xx) */
    SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /* 业务子状态码（格式：HTTP主码 + 3位子码） */
    // 参数校验类 (400xxx)
    PARAM_INVALID(400001, "参数异常"),
    JSON_PARSE_ERROR(400002, "json解析异常"),

    // 认证授权类 (401xxx)
    LOGIN_FAILED(401001, "用户名或密码错误"),
    TOKEN_EXPIRED(401002, "登录凭证已过期"),
    INVALID_TOKEN(401003, "无效的身份令牌"),
    ACCOUNT_LOCKED(401004, "账号已被锁定"),

    // 资源操作类 (403xxx/404xxx)
    RESOURCE_DELETED(404001, "资源已被删除"),
    OPERATION_REJECTED(403001, "无权限执行此操作"),
    RESOURCE_OCCUPIED(403002, "资源已被占用"),

    // 数据操作类 (422xxx)
    SQL_SYNTAX_ERROR(800, "sql异常"),
    DUPLICATE_DATA(422001, "数据记录已存在"),
    DATA_INTEGRITY_ERROR(422002, "数据完整性冲突"),
    DATA_NOT_FOUND(422003, "查询数据不存在"),

    // 文件操作类 (413xxx/415xxx)
    FILE_TOO_LARGE(413001, "文件大小超过限制"),
    UNSUPPORTED_FILE_TYPE(415001, "不支持的文件类型"),
    FILE_UPLOAD_FAILED(400101, "文件上传失败"),
    FILE_NOT_FOUND(400102, "文件不存在"),

    // 第三方服务类 (502xxx)
    AI_SERVICE_ERROR(502001, "AI服务调用失败"),
    SMS_SERVICE_ERROR(502002, "短信服务异常"),
    EMAIL_SERVICE_ERROR(502003, "邮件发送失败"),

    //操作
    ADD_FAIL(600, "添加失败"),
    UPDATE_FAIL(601, "修改失败"),
    DELETE_FAIL(602, "删除失败"),
    QUERY_FAIL(603, "删除失败"),
    REGISTER_FAIL(604, "注册失败"),


    //用户

    NOT_LOGIN(401, "未登录"),
    USER_NOT_EXIST(701, "用户不存在"),
    LOGIN_FAIL(702, "账号或密码错误"),
    USER_EXIST(703, "用户已存在"),
    TOKEN_EXPIRE(704, "Token 无效或已过期"),



    // 邮箱验证码
    EMAIL_NOT_EXIT(801, "邮箱格式错误"),
    EMAIL_ERROR(802, "邮箱验证码有误"),
    EMAIL_INVALID(803, "邮箱验证码已过期"),
    EMAIL_SEND_ERROR(804, "邮箱验证码发送失败"),

    // 图片验证码
    IMAGE_CODE_ERROR(811, "图片验证码有误"),
    IMAGE_CODE_INVALID(812, "图片验证码已过期"),
    IMAGE_CODE_GENERATE_ERROR(813, "验证码生成失败"),

    NOT_NULL(781, "不能为空"),


    // 文件
    FILE_UPLOAD_ERROR(1000, "上传失败,上传文件过大!"),
    FILE_MAX_ERROR(1001, "上传失败,上传文件过大!"),
    FILE_NULL_ERROR(1002, "上传失败,请选择至少一个文件!"),
    FILE_TOTAL_MAX_ERROR(1003, "上传失败,总文件大小不能超过50MB!"),
    FILE_DELETE_ERROR(1004, "删除文件失败!"),




    ;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
