package com.netease.yunxin.kit.meeting.sampleapp.data;

public class Response<T> {

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public final int code;

    public final String msg;

    public final T data;
}
