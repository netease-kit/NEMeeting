// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import static com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator.AuthStateChangeListener.AUTHORIZED;
import static com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator.AuthStateChangeListener.AUTHORIZING;
import static com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator.AuthStateChangeListener.AUTHOR_FAIL;
import static com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.netease.yunxin.kit.meeting.sampleapp.data.Response;
import com.netease.yunxin.kit.meeting.sampleapp.data.SDKAuthInfo;
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils;
import com.netease.yunxin.kit.meeting.sdk.NEAuthListener;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import java.util.concurrent.atomic.AtomicInteger;

/** 负责管理会议SDK的登录、登出 */
public class SdkAuthenticator implements SdkInitializer.InitializeListener {

  private static final String TAG = "SdkAuthenticator";

  private static final SdkAuthenticator INSTANCE = new SdkAuthenticator();

  public static SdkAuthenticator getInstance() {
    return INSTANCE;
  }

  private static final String KEY_LOGIN_SUCCEED = "LOGIN_SUCCEED";
  private static final String KEY_ACCOUNT = "ACCOUNT";
  private static final String KEY_PWD = "PWD";
  public static final String KEY_NICK_NAME = "NICK_NAME";

  private Context context;

  private final AtomicInteger state = new AtomicInteger(UN_AUTHORIZE);
  private AuthStateChangeListener authStateChangeListener;

  private SdkAuthenticator() {}

  public void initialize(Context context) {
    this.context = context;
    SdkInitializer.getInstance().addListener(this);
  }

  @Override
  public void onInitialized(int initializeIndex) {
    if (initializeIndex == 1) {
      tryAutoLogin();
    }
    NEMeetingKit.getInstance()
        .addAuthListener(
            new NEAuthListener() {
              @Override
              public void onKickOut() {
                onKickedOut();
              }

              @Override
              public void onAuthInfoExpired() {
                SdkAuthenticator.this.onAuthInfoExpired();
              }

              @Override
              public void onReconnected() {
                SdkAuthenticator.this.onReconnected();
              }
            });
  }

  public void setAuthStateChangeListener(AuthStateChangeListener authStateChangeListener) {
    this.authStateChangeListener = authStateChangeListener;
    if (state.get() > UN_AUTHORIZE) {
      notifyStateChanged();
    }
  }

  private void notifyStateChanged() {
    Log.i(TAG, "notifyStateChanged: " + state.get());
    if (authStateChangeListener != null) {
      authStateChangeListener.onAuthStateChanged(state.get());
    }
  }

  public boolean isAuthorized() {
    return state.get() == AUTHORIZED;
  }

  public int getState() {
    return state.get();
  }

  public void loginWithNEMeeting(final String account, final String pwd) {
    if (state.get() == AUTHORIZED) {
      Toast.makeText(context, "您已登录", Toast.LENGTH_SHORT).show();
    } else {
      state.set(AUTHORIZING);
      notifyStateChanged();
      handleLoginWithNEMeeting(account, pwd);
    }
  }

  @SuppressLint("StaticFieldLeak")
  public void loginWithSDKToken(final String accountId, final String accountToken) {
    NEMeetingKit.getInstance()
        .login(
            accountId,
            accountToken,
            new ToastCallback<Void>(context, "登录") {
              @Override
              public void onResult(int resultCode, String resultMsg, Void resultData) {
                super.onResult(resultCode, resultMsg, resultData);
                onLoginResult(resultCode, null, null);
              }
            });
  }

  public void logout(boolean manual) {
    if (state.get() == AUTHORIZED) {
      NEMeetingKit.getInstance()
          .logout(
              new ToastCallback<Void>(context, "注销") {
                @Override
                public void onResult(int resultCode, String resultMsg, Void resultData) {
                  //手动退出登录才进行toast提示
                  if (manual) super.onResult(resultCode, resultMsg, resultData);
                  if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                    if (state.compareAndSet(AUTHORIZED, UN_AUTHORIZE)) {
                      SPUtils.getInstance().remove(KEY_ACCOUNT);
                      SPUtils.getInstance().remove(KEY_NICK_NAME);
                      notifyStateChanged();
                    }
                    SPUtils.getInstance().put(KEY_LOGIN_SUCCEED, false);
                  }
                }
              });
    }
  }

  private void handleResponse(String account, String pwd, Response<SDKAuthInfo> result) {
    if (result != null && result.code == 200 && result.data != null) {
      NEMeetingKit.getInstance()
          .login(
              result.data.account,
              result.data.token,
              new ToastCallback<Void>(context, "登录") {
                @Override
                public void onResult(int resultCode, String resultMsg, Void resultData) {
                  super.onResult(resultCode, resultMsg, resultData);
                  onLoginResult(
                      resultCode,
                      () -> SPUtils.getInstance().put(KEY_ACCOUNT, account).put(KEY_PWD, pwd),
                      null);
                }
              });
    } else {
      state.set(AUTHOR_FAIL);
      notifyStateChanged();
      Toast.makeText(
              context,
              result != null && !TextUtils.isEmpty(result.msg) ? result.msg : "登录失败，请检查网络连接后重试！",
              Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void handleLoginWithNEMeeting(String account, String pwd) {
    NEMeetingKit.getInstance()
        .loginWithNEMeeting(
            account,
            pwd,
            new ToastCallback<Void>(context, "登录") {
              @Override
              public void onResult(int resultCode, String resultMsg, Void resultData) {
                super.onResult(resultCode, resultMsg, resultData);
                onLoginResult(
                    resultCode,
                    () -> SPUtils.getInstance().put(KEY_ACCOUNT, account).put(KEY_PWD, pwd),
                    null);
              }
            });
  }

  private void tryAutoLogin() {
    if (SPUtils.getInstance().getBoolean(KEY_LOGIN_SUCCEED)) {
      NEMeetingKit.getInstance()
          .tryAutoLogin(
              new ToastCallback<Void>(context, "自动登录") {
                @Override
                public void onResult(int resultCode, String resultMsg, Void resultData) {
                  super.onResult(resultCode, resultMsg, resultData);
                  onLoginResult(resultCode, null, null);
                }
              });
    }
  }

  private void onLoginResult(int code, Runnable onSuccessAction, Runnable onFailAction) {
    final boolean success;
    if (code == NEMeetingError.ERROR_CODE_SUCCESS) {
      success = true;
      state.set(AUTHORIZED);
      if (onSuccessAction != null) {
        onSuccessAction.run();
      }
    } else {
      success = false;
      state.set(AUTHOR_FAIL);
      if (onFailAction != null) {
        onFailAction.run();
      }
    }
    SPUtils.getInstance().put(KEY_LOGIN_SUCCEED, success);
    notifyStateChanged();
  }

  private void onKickedOut() {
    Log.i(TAG, "onKickOut");
    Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
    SdkAuthenticator.getInstance().logout(false);
  }

  private void onAuthInfoExpired() {
    Log.i(TAG, "onAuthInfoExpired");
    Toast.makeText(context, "登录状态已过期，请重新登录", Toast.LENGTH_SHORT).show();
    SdkAuthenticator.getInstance().logout(false);
  }

  private void onReconnected() {
    Log.i(TAG, "onReconnected");
    Toast.makeText(context, "IM重连成功", Toast.LENGTH_SHORT).show();
  }

  public static String getAccount() {
    return getAccount(null, null);
  }

  public static String getAccount(String defaultAccount) {
    return getAccount(defaultAccount, null);
  }

  public static String getAccount(String defaultAccount, Integer maxLength) {
    if (defaultAccount == null || defaultAccount.length() == 0) {
      defaultAccount = "xxxx";
    }
    String nickName;
    nickName = SPUtils.getInstance().getString(KEY_NICK_NAME);
    if (TextUtils.isEmpty(nickName)) {
      nickName = SPUtils.getInstance().getString(KEY_ACCOUNT, defaultAccount);
      if (maxLength != null) {
        nickName = nickName.substring(Math.max(nickName.length() - maxLength, 0));
      }
    }
    return nickName;
  }

  public interface AuthStateChangeListener {

    /** 授权失败 */
    int AUTHOR_FAIL = -2;

    /** 未授权 */
    int UN_AUTHORIZE = -1;

    /** 授权中 */
    int AUTHORIZING = 0;

    /** 已授权 */
    int AUTHORIZED = 1;

    void onAuthStateChanged(int state);
  }
}
