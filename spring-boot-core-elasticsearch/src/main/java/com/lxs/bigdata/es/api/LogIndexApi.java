package com.lxs.bigdata.es.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志记录到logstash api
 */
@RestController
@RequestMapping(value = "/api/log/")
public class LogIndexApi {
    private static final Logger logger = LoggerFactory.getLogger(LogIndexApi.class);

    @RequestMapping(value = "index", method = RequestMethod.GET)
    @ResponseBody
    public Object index() {
        logger.info("进入了index方法");
        logger.info("开始执行业务逻辑");
        logger.info("index方法结束");
        return "success";
    }
}
