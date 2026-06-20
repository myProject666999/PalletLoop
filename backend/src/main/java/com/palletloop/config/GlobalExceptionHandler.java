package com.palletloop.config;

import com.palletloop.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static String extractFriendlyMessage(Throwable e) {
        Throwable root = findRootCause(e);
        String msg = "操作失败";
        String m = (root != null && root.getMessage() != null) ? root.getMessage() : "";
        if (m.contains("uk_code")) {
            msg = "编码已存在，请更换后重试";
        } else if (m.contains("Duplicate entry")) {
            msg = "数据重复，请检查后重试";
        } else if (m.toLowerCase().contains("foreign key")) {
            msg = "该数据已被引用，无法操作";
        } else if (m.contains("cannot be null")) {
            msg = "必填字段不能为空";
        }
        return msg;
    }

    private static Throwable findRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("唯一键冲突: {}", e.getMessage());
        return Result.error(extractFriendlyMessage(e));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("数据完整性冲突: {}", e.getMessage());
        return Result.error(extractFriendlyMessage(e));
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleDataAccessException(DataAccessException e) {
        log.warn("数据库访问异常: {}", e.getMessage());
        return Result.error(extractFriendlyMessage(e));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.warn("数据库约束冲突: {}", e.getMessage());
        return Result.error(extractFriendlyMessage(e));
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleSQLException(SQLException e) {
        log.warn("SQL异常: {}", e.getMessage());
        return Result.error(extractFriendlyMessage(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(msg);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        String friendly = extractFriendlyMessage(e);
        if (!"操作失败".equals(friendly)) {
            return Result.error(friendly);
        }
        return Result.error("系统异常：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
    }
}
