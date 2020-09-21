package com.lxs.bigdata.es.config;

import com.lxs.bigdata.es.common.CommonResult;
import com.lxs.bigdata.es.common.ResultCode;
import com.lxs.bigdata.es.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

@ControllerAdvice
@Slf4j
public class ESExceptionAdvice {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public CommonResult<String> exceptionHandler(Exception exception) {
        log.error("服务器未知错误 ", exception);
        return CommonResult.failed(ResultCode.FAILED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public CommonResult<String> runtimeExceptionHandler(RuntimeException exception, HttpServletRequest req) {
        log.error("服务器运行时错误 ", exception);
        CommonResult<String> commonResult = CommonResult.failed(ResultCode.ES_FAILED);
        commonResult.setCode(ResultCode.ES_FAILED.getCode());
        if (exception instanceof CreateIndexFailedException) {
            commonResult.setMessage("创建索引出错");
        } else if (exception instanceof IndicesExistsFailedException) {
            commonResult.setMessage("判断索引是否存在出错");
        } else if (exception instanceof PutMappingFailedException) {
            commonResult.setMessage("添加索引类型映射出错");
        } else if (exception instanceof SearchQueryBuildException) {
            commonResult.setMessage("构建搜索DSL出错");
        } else if (exception instanceof SearchResultBuildException) {
            commonResult.setMessage("出错搜索结果出错");
        }
        commonResult.setData("");
        //使用HttpServletRequest中的header检测请求是否为ajax, 如果是ajax则返回json,
        // 如果为非ajax则返回view(即ModelAndView)
        String contentTypeHeader = req.getHeader("Content-Type");
        String acceptHeader = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        if ((contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            return commonResult;
        } else {
//            ModelAndView modelAndView = new ModelAndView();
//            modelAndView.addObject("msg", exception.getMessage());
//            modelAndView.addObject("url", req.getRequestURL());
//            modelAndView.addObject("stackTrace", exception.getStackTrace());
//            modelAndView.setViewName("error");
//            return modelAndView;
            return CommonResult.failed(ResultCode.ES_FAILED);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(KafkaConsumerException.class)
    public CommonResult<String> createIndexExceptionHandler(KafkaConsumerException exception, HttpServletRequest req) {
        log.error("消费kafka数据时错误 ", exception);
        CommonResult<String> commonResult = CommonResult.failed(ResultCode.KAFKA_FAILED);
        commonResult.setCode(ResultCode.KAFKA_FAILED.getCode());
        commonResult.setMessage("出错kafka队列中数据出错");
        commonResult.setData("");
        String contentTypeHeader = req.getHeader("Content-Type");
        String acceptHeader = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        if ((contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            return commonResult;
        }
        return CommonResult.failed(ResultCode.KAFKA_FAILED);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public CommonResult handleValidationException(ValidationException e) {
        log.error("参数验证失败", e);
        return CommonResult.validateFailed("参数验证失败");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CommonResult handleBindException(BindException e) {
        log.error("参数绑定失败", e);
        BindingResult result = e.getBindingResult();
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            if (error != null) {
                String field = error.getField();
                String code = error.getDefaultMessage();
                String message = String.format("%s:%s", field, code);
                return CommonResult.validateFailed("参数绑定失败," + message);
            }
        }
        return CommonResult.validateFailed("参数绑定失败");
    }
}
