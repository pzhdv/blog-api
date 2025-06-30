package cn.pzhdv.blog.exception;


import cn.pzhdv.blog.result.Result;
import cn.pzhdv.blog.result.ResultCode;
import cn.pzhdv.blog.result.ResultUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLSyntaxErrorException;

/**
 * @author PanZonghui
 * @Version: 1.0
 * @Description 全局异常处理器
 * 用于捕获和处理应用中抛出的各种异常，返回统一的错误响应
 * @since 2025-04-24 13:10:16
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有未处理的异常（兜底处理）
     * 当没有其他更具体的异常处理器匹配时，此方法将被调用
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("Exception异常捕获了");
        log.error("未捕获异常: {} [{}]", path, request.getMethod(), e);
        return ResultUtil.error(ResultCode.SERVER_ERROR.code(), ResultCode.SERVER_ERROR.message());
    }

    /**
     * 捕获自定义业务异常
     * 业务逻辑中抛出的异常，通常包含具体的错误代码和消息
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("业务异常(全局异常捕获): [code={}] {} {}", e.getCode(), path, e.getMessage());
        return ResultUtil.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获404异常（接口不存在）
     * 当请求的接口路径不存在时，Spring会抛出此异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("接口不存在(全局异常捕获): {} [{}] {}", path, request.getMethod(), e.getMessage());
        return ResultUtil.error(ResultCode.NOT_FOUND.code(), ResultCode.NOT_FOUND.message());
    }

    /**
     * 捕获请求方法不支持异常
     * 当请求的HTTP方法不被支持时，Spring会抛出此异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("方法不允许(全局异常捕获): {} [{}], 支持的方法: {}", path, request.getMethod(), e.getSupportedHttpMethods());
        return ResultUtil.error(ResultCode.METHOD_NOT_ALLOWED.code(), ResultCode.METHOD_NOT_ALLOWED.message());
    }

    /**
     * 捕获文件上传大小超限异常
     * 当上传的文件大小超过配置的最大值时，Spring会抛出此异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("文件大小超过限制(全局异常捕获): {} - 最大允许: {}", path, e.getMaxUploadSize());
        return ResultUtil.error(ResultCode.FILE_UPLOAD_ERROR.code(), ResultCode.FILE_UPLOAD_ERROR.message());
    }


    /**
     * 捕获SQL语法异常
     * 当执行SQL语句时发生语法错误，JDBC会抛出此异常
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result handleSQLSyntaxErrorException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("sql语法异常异常--" + "请求URI:" + path + "异常为:" + e.getMessage());
        return ResultUtil.error(ResultCode.SQL_SYNTAX_ERROR.code(), ResultCode.SQL_SYNTAX_ERROR.message());
    }

    /**
     * 捕获数据访问异常
     * 当与数据库交互时发生错误，Spring的JdbcTemplate等会抛出此异常
     */
    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("数据访问异常: {} [{}]", path, request.getMethod(), e);
        return ResultUtil.error(ResultCode.SERVER_ERROR.code(), ResultCode.SERVER_ERROR.message());
    }

    /**
     * 捕获参数校验异常
     * 当使用Spring的@Valid注解校验请求参数失败时，会抛出此异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("参数校验异常: {} [{}]", path, request.getMethod(), e);
        // 获取校验失败的第一个错误信息
        return ResultUtil.error(ResultCode.PARAM_INVALID.code(), ResultCode.PARAM_INVALID.message());
    }

    /**
     * 捕获JSON解析异常
     * 当解析请求体中的JSON数据失败时，Spring会抛出此异常
     */
    @ExceptionHandler(JsonProcessingException.class)
    public Result handleJsonProcessingException(JsonProcessingException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("JSON解析异常: {} [{}]", path, request.getMethod(), e);
        return ResultUtil.error(ResultCode.JSON_PARSE_ERROR.code(), ResultCode.JSON_PARSE_ERROR.message());
    }

}
