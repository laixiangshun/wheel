package com.lxs.bigdata.pay.api;

import com.google.gson.Gson;
import com.lxs.bigdata.pay.vo.CommonResult;
import com.lxs.bigdata.pay.model.PayRequest;
import com.lxs.bigdata.pay.model.PayResponse;
import com.lxs.bigdata.pay.model.RefundRequest;
import com.lxs.bigdata.pay.model.RefundResponse;
import com.lxs.bigdata.pay.service.BestPayServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 支付api
 *
 * @author lxs
 */
@Api(value = "统一支付API", tags = "统一支付API")
@RestController
@RequestMapping(value = "/pay-center/")
public class BestPayController {

    private static final Gson GSON = new Gson();

    @Autowired
    private BestPayServiceImpl payService;

    @RequestMapping(value = "prePay", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "统一支付", notes = "统一支付")
    public CommonResult pay(@RequestBody @Valid PayRequest payRequest) {
        PayResponse payResponse = payService.pay(payRequest);
        return CommonResult.success(GSON.toJson(payResponse));
    }

    @RequestMapping(value = "refund", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "统一支付退款", notes = "统一支付退款")
    public CommonResult refund(@RequestBody @Valid RefundRequest refundRequest) {
        RefundResponse refundResponse = payService.refund(refundRequest);
        return CommonResult.success(GSON.toJson(refundResponse));
    }

    /**
     * 异步通知
     */
    @PostMapping("notify")
    @ApiOperation(value = "统一支付异步通知回调", notes = "统一支付异步通知回调")
    public String notify(@RequestBody String notifyData) {
        PayResponse payResponse = payService.asyncNotify(notifyData);
        return "";
    }
}
