/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.app.Application;

public class MeetingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SdkAuthenticator.getInstance().initialize(this);
        SdkInitializer.getInstance().startInitialize(this);
    }
}
