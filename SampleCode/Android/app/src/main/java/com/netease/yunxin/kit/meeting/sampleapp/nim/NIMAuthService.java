// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.nim;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.utils.ProcessUtils;
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils;
import com.netease.yunxin.kit.meeting.utils.LogUtils;
import java.io.File;

public class NIMAuthService implements Observer<StatusCode> {

  private static final String TAG = "NIMAuthService";

  private static NIMAuthService sInstance;

  public static synchronized NIMAuthService getInstance() {
    if (sInstance == null) {
      sInstance = new NIMAuthService();
    }
    return sInstance;
  }

  private NIMAuthService() {}

  private MutableLiveData<StatusCode> _statusLiveData;

  public LiveData<StatusCode> getStatusLiveData() {
    ensureNimInitialized();
    if (_statusLiveData == null) {
      _statusLiveData = new MutableLiveData<>(NIMClient.getStatus());
      NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, true);
    }
    return _statusLiveData;
  }

  @Override
  public void onEvent(StatusCode statusCode) {
    _statusLiveData.setValue(statusCode);
  }

  //如果开启了NIM复用，则需要单独进行NIM的初始化
  //正常情况下不需要单独手动进行初始化
  private void ensureNimInitialized() {
    try {
      if (NIMClient.getService(AuthService.class) != null) {
        LogUtils.i(TAG, "nim sdk has been initialized");
        return;
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    LogUtils.i(TAG, "start initialize nim sdk");
    SDKOptions sdkOptions = SDKOptions.DEFAULT;
    sdkOptions.disableAwake = true;
    final Context context = MeetingApplication.getInstance();
    File externalFilesDir = context.getExternalFilesDir(null);
    sdkOptions.sdkStorageRootPath =
        externalFilesDir != null ? externalFilesDir.getAbsolutePath() : null;
    NIMClient.config(context, null, sdkOptions);
    if (ProcessUtils.isMainProcess(context)) {
      NIMClient.initSDK();
    }
  }

  public void login(String appKey, String imAccid, String imToken) {
    ensureNimInitialized();
    NIMClient.getService(AuthService.class).login(new LoginInfo(imAccid, imToken, appKey));
  }

  public void logout() {
    try {
      NIMClient.getService(AuthService.class).logout();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public boolean isReuseNIMEnabled() {
    return SPUtils.getInstance().getBoolean("meeting-reuse-nim", false);
  }

  public void setReuseNIMEnabled(boolean enable) {
    SPUtils.getInstance().put("meeting-reuse-nim", enable);
  }
}
