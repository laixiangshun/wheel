package com.lxs.bigdat.deadqueue.exception;

import com.lxs.bigdat.deadqueue.common.CommonResult;
import com.lxs.bigdat.deadqueue.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 通用异常处理类
 */
@Slf4j
@ControllerAdvice
public class DeadQueueExceptionAdvice {

    @ResponseBody
    @ExceptionHandler
    public CommonResult exceptionHandler(DeadQueueException deadQueueException) {
        log.error("死信队列异常：code={},msg={},errMsg={}", deadQueueException.getCode(), deadQueueException.getMsg(), deadQueueException.getErrorMsg());
        return CommonResult.failed(Long.parseLong(deadQueueException.getCode()), deadQueueException.getMessage(), deadQueueException.getErrorMsg());
    }

    @ResponseBody
    @ExceptionHandler
    public CommonResult exceptionHandler(Exception exception) {
        if (exception instanceof ForbiddenException) {
            ForbiddenException forbiddenException = (ForbiddenException) exception;
            String code = forbiddenException.getCode();
            String msg = forbiddenException.getMsg();
            String errorMsg = forbiddenException.getErrorMsg();
            log.error("权限异常：code={},msg={},errMsg={}", code, msg, errorMsg, forbiddenException);
            return CommonResult.failed(Long.parseLong(code), msg, errorMsg);
        } else if (exception instanceof UnAuthException) {
            UnAuthException authException = (UnAuthException) exception;
            String code = authException.getCode();
            String msg = authException.getMsg();
            String errorMsg = authException.getErrorMsg();
            log.error("登录异常：code={},msg={},errMsg={}", code, msg, errorMsg, authException);
            return CommonResult.failed(Long.parseLong(code), msg, errorMsg);
        } else if (exception instanceof ValidateException) {
            ValidateException validateException = (ValidateException) exception;
            String code = validateException.getCode();
            String msg = validateException.getMsg();
            String errorMsg = validateException.getErrorMsg();
            log.error("参数验证异常：code={},msg={},errMsg={}", code, msg, errorMsg, validateException);
            return CommonResult.failed(Long.parseLong(code), msg, errorMsg);
        } else if (exception instanceof SystemException) {
            SystemException systemException = (SystemException) exception;
            String code = systemException.getCode();
            String msg = systemException.getMsg();
            String errorMsg = systemException.getErrorMsg();
            log.error("系统异常：code={},msg={},errMsg={}", code, msg, errorMsg, systemException);
            return CommonResult.failed(Long.parseLong(code), msg, errorMsg);
        } else {
            log.error("未知异常：{}", exception.getMessage(), exception);
            return CommonResult.failed(ResultCode.SYSTEM.getCode(), null, ResultCode.SYSTEM.getMessage());
        }
    }
}
