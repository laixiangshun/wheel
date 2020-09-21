package com.lxs.bigdata.elk.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class LogException {

    private static final Logger logger = LoggerFactory.getLogger(LogException.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(value = RuntimeException.class)
    public void runtimeException(RuntimeException e) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put("level", "ERROR");
        data.put("message", e.getMessage());
        data.put("trace", e.getStackTrace());
        logger.error(mapper.writeValueAsString(data), e);
    }
}
