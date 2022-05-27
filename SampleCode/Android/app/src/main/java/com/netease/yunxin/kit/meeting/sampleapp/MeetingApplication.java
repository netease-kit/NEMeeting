/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp;

import android.app.Application;

import com.netease.yunxin.kit.meeting.sampleapp.data.ServerConfig;
import com.netease.yunxin.kit.meeting.sampleapp.data.ServerConfigs;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;

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
