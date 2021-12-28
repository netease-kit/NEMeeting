package com.netease.meeting.http;

import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * @author HJ
 * @date 2021/11/18
 **/
public interface ServiceClient {

    /**
     * post请求
     * @param url  url
     * @param body post body内容
     * @param header 请求头
     * @param typeToken 返回类型包装类
     * @return res
     */
    <T> T postForEntity(String url, String body, Map<String,String> header, TypeToken<T> typeToken);

    /**
     * 关闭客户端
     */
    void shutdown();

}
