package com.netease.meetinglib.demo.data;

public interface DataRepository {

    /**
     * 通过用户账号密码获取SDK登录鉴权用的信息
     * @param account 用户名
     * @param pwd     密码
     * @return
     */
    Response<SDKAuthInfo> getSDKAuthInfo(String account, String pwd);

}
