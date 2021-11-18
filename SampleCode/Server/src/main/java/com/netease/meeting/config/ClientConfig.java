package com.netease.meeting.config;

import com.netease.meeting.util.VersionInfoUtil;

/**
 * @author HJ
 * @date 2021/11/17
 **/
public class ClientConfig {
    public static final String DEFAULT_USER_AGENT = VersionInfoUtil.getUserAgent();
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_MAX_CONNECTIONS = 1024;
    public static final long DEFAULT_IDLE_CONNECTION_TIME = 60 * 1000;

    protected String userAgent = DEFAULT_USER_AGENT;

    protected int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    protected int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    protected int maxConnections = DEFAULT_MAX_CONNECTIONS;
    protected long idleConnectionTime = DEFAULT_IDLE_CONNECTION_TIME;


    public ClientConfig() {
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public long getIdleConnectionTime() {
        return idleConnectionTime;
    }

    public void setIdleConnectionTime(long idleConnectionTime) {
        this.idleConnectionTime = idleConnectionTime;
    }
}
