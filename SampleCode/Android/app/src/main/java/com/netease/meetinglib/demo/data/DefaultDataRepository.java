package com.netease.meetinglib.demo.data;

import android.util.Log;

import kotlin.NotImplementedError;

public class DefaultDataRepository implements DataRepository {

    private static final String TAG = "DefaultDataRepository";

    @Override
    public Response<SDKAuthInfo> getSDKAuthInfo(String account, String pwd) {
        //
        //TODO: 自定义实现account、token获取的逻辑
        //
        Log.e(TAG, "会议账号不存在", new NotImplementedError("账号逻辑未实现"));
        return new Response<>(404, "会议账号不存在", null);
    }
}
