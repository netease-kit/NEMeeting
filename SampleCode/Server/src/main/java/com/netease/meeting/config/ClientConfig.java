package com.netease.meeting.config;


/**
 * @author HJ
 * @date 2021/11/17
 **/
public class ClientConfig {

    /**
     * 从连接池中获取连接的超时时间，默认不超时
     * 单位：毫秒
     */
    protected int connectionRequestTimeout = -1;
    /**
     * 建立连接的超时时间,默认为5000毫秒
     * 单位：毫秒
     */
    protected int connectionTimeout = 5 * 1000;
    /**
     * socket超时时间，默认5000毫秒
     * 单位：毫秒
     */
    protected int socketTimeout = 5 * 1000;
    /**
     * 允许打开的最大HTTP连接数。默认为1024
     */
    protected int maxConnections = 1024;

    /**
     * 	连接空闲超时时间，超时则关闭连接，默认为60000毫秒
     * 	单位：毫秒
     */
    protected long idleConnectionTime = 60 * 1000;
    /**
     * 最大重试次数，默认3次
     */
    protected int maxRetryTime = 3;

    public ClientConfig() {
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

    public int getMaxRetryTime() {
        return maxRetryTime;
    }

    public void setMaxRetryTime(int maxRetryTime) {
        this.maxRetryTime = maxRetryTime;
    }
}
