package com.netease.meeting;

import com.netease.meeting.config.ClientConfig;

/**
 * @author HJ
 * @date 2021/11/17
 **/
public class NEMeetingClientBuilder {
    /**
     *
     * @param endpoint 域名
     * @param appKey  appKey
     * @param appSecret appSecret
     * @return client
     */
    public static NEMeetingClient build(String endpoint, String appKey, String appSecret) {
        return new DefaultNEMeetingClient(endpoint,appKey,appSecret,null);
    }

    /**
     * 带配置的构造类，可以配置线程池大小、超时时间等
     * @param endpoint 域名
     * @param appKey  appKey
     * @param appSecret appSecret
     * @param config 配置封装类
     * @return client
     */
    public static NEMeetingClient build(String endpoint, String appKey, String appSecret, ClientConfig config) {
        return new DefaultNEMeetingClient(endpoint,appKey,appSecret,config);
    }

}
