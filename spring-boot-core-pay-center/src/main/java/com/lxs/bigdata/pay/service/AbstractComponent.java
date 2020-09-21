package com.lxs.bigdata.pay.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lxs
 */
@Slf4j
public abstract class AbstractComponent {

    protected abstract String getPayTypeCode();
}
