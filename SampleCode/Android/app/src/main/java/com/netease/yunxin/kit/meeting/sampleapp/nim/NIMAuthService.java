// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.nim;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.v2.V2NIMError;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginListener;
import com.netease.nimlib.sdk.v2.auth.V2NIMLoginService;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginClientChange;
import com.netease.nimlib.sdk.v2.auth.enums.V2NIMLoginStatus;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMKickedOfflineDetail;
import com.netease.nimlib.sdk.v2.auth.model.V2NIMLoginClient;
import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.utils.ProcessUtils;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.utils.LogUtils;
import java.io.File;
import java.util.List;

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

  private MutableLiveData<V2NIMLoginStatus> _statusLiveData;

  public LiveData<V2NIMLoginStatus> getStatusLiveData() {
    ensureNimInitialized();
    if (_statusLiveData == null) {
      _statusLiveData =
          new MutableLiveData<>(NIMClient.getService(V2NIMLoginService.class).getLoginStatus());
      NIMClient.getService(V2NIMLoginService.class).addLoginListener(v2NIMLoginListener);
    }
    return _statusLiveData;
  }

  public String getLoginUser() {
    return NIMClient.getService(V2NIMLoginService.class).getLoginUser();
  }

  //如果开启了NIM复用，则需要单独进行NIM的初始化
  //正常情况下不需要单独手动进行初始化
  private void ensureNimInitialized() {
    try {
      if (NIMClient.getService(V2NIMLoginService.class) != null) {
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
    if (ProcessUtils.isMainProcess(context)) {
      NIMClient.initV2(context, sdkOptions);
    }
  }

  public void login(String appKey, String imAccid, String imToken, NECallback<Void> callback) {
    ensureNimInitialized();
    NIMClient.getService(V2NIMLoginService.class)
        .login(
            imAccid,
            imToken,
            null,
            unused ->
                callback.onResult(
                    NEMeetingError.ERROR_CODE_SUCCESS, NEMeetingError.ERROR_MSG_SUCCESS, null),
            error -> {
              LogUtils.i(TAG, "login fail: " + error.getCode());
              callback.onResult(error.getCode(), error.getDesc(), null);
            });
  }

  public void logout() {
    try {
      NIMClient.getService(V2NIMLoginService.class)
          .logout(
              unused -> {
                LogUtils.i(TAG, "logout success");
              },
              error -> LogUtils.i(TAG, "logout fail: " + error.getCode()));
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private V2NIMLoginListener v2NIMLoginListener =
      new V2NIMLoginListener() {

        @Override
        public void onLoginStatus(V2NIMLoginStatus status) {
          _statusLiveData.setValue(status);
        }

        @Override
        public void onLoginFailed(V2NIMError error) {}

        @Override
        public void onKickedOffline(V2NIMKickedOfflineDetail detail) {}

        @Override
        public void onLoginClientChanged(
            V2NIMLoginClientChange change, List<V2NIMLoginClient> clients) {}
      };
}
