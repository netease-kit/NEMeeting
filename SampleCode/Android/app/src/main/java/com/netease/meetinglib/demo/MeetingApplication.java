/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.app.Application;

import com.netease.meetinglib.demo.data.ServerConfig;
import com.netease.meetinglib.demo.data.ServerConfigs;
import com.netease.meetinglib.demo.log.LogUtil;
import com.netease.meetinglib.demo.nim.NIMInitializer;

public class MeetingApplication extends Application {
    private static MeetingApplication instance;

    private ServerConfig serverConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtil.init(this);

        serverConfig = ServerConfigs.INSTANCE.determineServerConfig(getString(R.string.appkey));
        LogUtil.log("MeetingApplication", serverConfig.toString());

        //注意：只在开启NIM复用时，才需要手动进行NIM的初始化操作，其他情况下一律忽略
        //NIMInitializer.getInstance().startInitialize(this);

        //初始化会议SDK登录状态监听
        SdkAuthenticator.getInstance().initialize(this);
        //初始化会议SDK
        SdkInitializer.getInstance().startInitialize(this);
    }

    public static MeetingApplication getInstance() {
        return instance;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
