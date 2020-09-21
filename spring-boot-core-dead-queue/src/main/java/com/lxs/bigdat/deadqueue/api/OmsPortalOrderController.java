package com.lxs.bigdat.deadqueue.api;

import com.lxs.bigdat.deadqueue.common.CommonResult;
import com.lxs.bigdat.deadqueue.dto.OrderParam;
import com.lxs.bigdat.deadqueue.service.OmsPortalOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "订单管理API", tags = "OmsPortalOrderController")
@RequestMapping(value = "/order")
public class OmsPortalOrderController {

    @Autowired
    private OmsPortalOrderService omsPortalOrderService;

    @ResponseBody
    @ApiOperation(value = "生成订单", notes = "下订单")
    @PostMapping(value = "/generateOrder")
    public CommonResult createOrder(@RequestBody OrderParam orderParam) {
        return omsPortalOrderService.generateOrder(orderParam);
    }

    @ResponseBody
    @ApiOperation(value = "取消订单", notes = "取消订单")
    @PostMapping(value = "/cancelOrder")
    public CommonResult cancelOrder(@RequestBody Long orderId) {
        return omsPortalOrderService.cancelOrder(orderId);
    }
}
