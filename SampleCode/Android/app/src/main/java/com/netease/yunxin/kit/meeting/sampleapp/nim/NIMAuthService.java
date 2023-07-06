// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.nim;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.utils.ProcessUtils;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.utils.LogUtils;
import java.io.File;

public class NIMAuthService {

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
      NIMClient.getService(AuthServiceObserver.class)
          .observeOnlineStatus(_statusLiveData::setValue, true);
    }
    return _statusLiveData;
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

  public void login(String appKey, String imAccid, String imToken, NECallback<LoginInfo> callback) {
    ensureNimInitialized();
    NIMClient.getService(AuthService.class)
        .login(new LoginInfo(imAccid, imToken, appKey))
        .setCallback(
            new RequestCallback<LoginInfo>() {
              @Override
              public void onSuccess(LoginInfo result) {
                callback.onResult(
                    NEMeetingError.ERROR_CODE_SUCCESS, NEMeetingError.ERROR_MSG_SUCCESS, result);
              }

              @Override
              public void onFailed(int code) {
                LogUtils.i(TAG, "login fail: " + code);
                callback.onResult(code, null, null);
              }

              @Override
              public void onException(Throwable exception) {
                callback.onResult(NEMeetingError.ERROR_CODE_FAILED, exception.getMessage(), null);
              }
            });
  }

  public void logout() {
    try {
      NIMClient.getService(AuthService.class).logout();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
