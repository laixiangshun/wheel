package com.lxs.bigdata.pay.rest;

import org.glassfish.jersey.SslConfigurator;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

/**
 * HttpsClient 使用与https请求
 *
 * @author lxs
 */
public class HttpsClient extends Client {

    public HttpsClient(String host) {
        this(host, null);
    }

    private HttpsClient(String host, String project) {
        SslConfigurator sslConfig = SslConfigurator.newInstance();
        SSLContext ssl = sslConfig.createSSLContext();
        this.target = ClientBuilder.newBuilder().sslContext(ssl).build().target(UriBuilder.fromUri("https://" + host).build());
        if (project != null) {
            this.target = this.target.path(project);
        }
    }

}
