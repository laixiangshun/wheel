package com.lxs.bigdata.pay.model.alipay;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.core.io.buffer.DefaultDataBufferFactory.DEFAULT_INITIAL_CAPACITY;

/**
 * 支付宝退款义务参数
 *
 * @author lxs
 */
@Data
public class AlipayRefundRequest {

    private String outTradeNo;

    private String tradeNo;

    private BigDecimal refundAmount;

    private String refundCurrency;

    private String refundReason;

    private String outRequestNo;

    private String operatorId;

    private String storeId;

    private String terminalId;

    private String orgPid;

    public Map<String, Object> getBizParam() {
        Map<String, Object> bizParam = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        bizParam.put("out_trade_no", this.outTradeNo);
        bizParam.put("trade_no", this.tradeNo);
        long refundAmount = this.refundAmount.setScale(2, RoundingMode.HALF_UP).longValue();
        bizParam.put("refund_amount", refundAmount);
        bizParam.put("refund_currency", this.refundCurrency);
        bizParam.put("refund_reason", this.refundReason);
        bizParam.put("out_request_no", this.outRequestNo);
        bizParam.put("operator_id", this.operatorId);
        bizParam.put("store_id", this.storeId);
        bizParam.put("terminal_id", this.terminalId);
        bizParam.put("org_pid", this.orgPid);
        return bizParam;
    }
}
