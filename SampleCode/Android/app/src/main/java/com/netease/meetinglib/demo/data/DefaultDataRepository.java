package com.netease.meetinglib.demo.data;

public class DefaultDataRepository implements DataRepository {

    @Override
    public Response<SDKAuthInfo> getSDKAuthInfo(String account, String pwd) {
        //
        //TODO: 自定义实现account、token获取的逻辑
        //
        throw new RuntimeException("Not Implemented!");
    }
}
