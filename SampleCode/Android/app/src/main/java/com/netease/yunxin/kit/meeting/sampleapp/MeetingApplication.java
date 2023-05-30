// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.app.Application;
import com.netease.yunxin.kit.meeting.sampleapp.data.ServerConfig;
import com.netease.yunxin.kit.meeting.sampleapp.data.ServerConfigs;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.utils.FileUtils;

public class MeetingApplication extends Application {

  private static final String TAG = "MeetingApplication";

  private static MeetingApplication instance;

  private ServerConfig serverConfig;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    LogUtil.init(this);

    serverConfig = ServerConfigs.INSTANCE.determineServerConfig(getString(R.string.appkey));
    LogUtil.log(TAG, serverConfig.toString());

    //初始化会议SDK登录状态监听
    SdkAuthenticator.getInstance().initialize(this);
    //初始化会议SDK
    SdkInitializer.getInstance().startInitialize(this);

    new Thread(
            () ->
                FileUtils.INSTANCE.copyAssetsToDst(
                    instance, "virtual", getFilesDir().getPath() + "/virtual"))
        .start();
  }

  public static MeetingApplication getInstance() {
    return instance;
  }

  public ServerConfig getServerConfig() {
    return serverConfig;
  }
}
