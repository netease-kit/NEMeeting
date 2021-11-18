package com.netease.meeting.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.netease.meeting.config.ClientConfig;
import com.netease.meeting.exception.NETMeetingException;
import com.netease.meeting.util.VersionInfoUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.Map;

/**
 * @author HJ
 * @date 2021/11/17
 **/
public class HttpClientComponent implements ServiceClient {
    private final Log log = LogFactory.getLog(getClass());
    private final CloseableHttpClient httpClient;
    private final Gson gson = new GsonBuilder().create();

    public HttpClientComponent(ClientConfig config) {
        // default connection config
        ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectTimeout(config.getConnectionTimeout());
        requestConfigBuilder.setSocketTimeout(config.getSocketTimeout());
        requestConfigBuilder.setConnectionRequestTimeout(config.getConnectionRequestTimeout());
        RequestConfig requestConfig = requestConfigBuilder.build();
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent(VersionInfoUtil.getUserAgent())
                .disableContentCompression()
                // 快速失败，有用户自行重试
                .disableAutomaticRetries()
                .setDefaultConnectionConfig(defaultConnectionConfig)
                .setMaxConnTotal(config.getMaxConnections())
                .build();
    }

    @Override
    public <T> T postForEntity(String url, String body, Map<String, String> header, TypeToken<T> typeToken) {
        HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        configHeader(httpPost, header);
        long watch = System.nanoTime();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody;
            if (statusCode == HttpStatus.SC_OK) {
                responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            } else {
                String httpDesc = response.getStatusLine().getReasonPhrase();
                throw new NETMeetingException(String.valueOf(statusCode), "connect error. " + httpDesc);
            }
            EntityUtils.consumeQuietly(response.getEntity());
            if (log.isDebugEnabled()) {
                log.debug("url: " + httpPost.getURI() + ", httpCode: " + statusCode + ",  responseBody: " + responseBody + ", rt: {}" + (System.nanoTime() - watch) / 1000 / 1000);
            }
            return gson.fromJson(responseBody, typeToken.getType());
        } catch (NETMeetingException ne) {
            throw ne;
        } catch (Exception e) {
            log.error("NETMeeting api connect error. url: " + url, e);
            throw new NETMeetingException(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (final IOException ioe) {
                // ignore
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            //ignore
        }
    }


    private void configHeader(HttpPost httpPost, Map<String, String> header) {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
    }
}
