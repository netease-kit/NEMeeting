/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.nim;

import android.content.Context;
import android.util.Log;

import com.netease.meetinglib.demo.utils.ProcessUtils;
import com.netease.meetinglib.demo.utils.SPUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;

import java.io.File;

public class NIMInitializer {

    private static final String TAG = "NIMInitializer";

    private static NIMInitializer sInstance;

    public synchronized static NIMInitializer getInstance() {
        if (sInstance == null) {
            sInstance = new NIMInitializer();
        }
        return sInstance;
    }

    private NIMInitializer() {}

    //如果开启了NIM复用，则需要单独进行NIM的初始化
    //正常情况下不需要单独手动进行初始化
    public void startInitialize(Context context) {
        if (isReuseNIMEnabled()) {
            Log.i(TAG, "reuse nim is enabled");
            SDKOptions sdkOptions = SDKOptions.DEFAULT;
            sdkOptions.disableAwake = true;
            sdkOptions.useAssetServerAddressConfig = SPUtils.getInstance().getBoolean("use-asset-server-config");
            File externalFilesDir = context.getExternalFilesDir(null);
            sdkOptions.sdkStorageRootPath = externalFilesDir != null ? externalFilesDir.getAbsolutePath() : null;
            NIMClient.config(context, null, sdkOptions);
            if (ProcessUtils.isMainProcess(context)) {
                NIMClient.initSDK();
            }
        }
    }

    public boolean isReuseNIMEnabled() {
        return SPUtils.getInstance().getBoolean("meeting-reuse-nim", false);
    }

    public void setReuseNIMEnabled(boolean enable) {
        SPUtils.getInstance().put("meeting-reuse-nim", enable);
    }
}
