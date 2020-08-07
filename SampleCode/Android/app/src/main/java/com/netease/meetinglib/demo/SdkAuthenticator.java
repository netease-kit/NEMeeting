/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netease.meetinglib.demo.data.DataRepository;
import com.netease.meetinglib.demo.data.DefaultDataRepository;
import com.netease.meetinglib.demo.data.Response;
import com.netease.meetinglib.demo.data.SDKAuthInfo;
import com.netease.meetinglib.sdk.NEAuthListener;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;

import java.util.concurrent.atomic.AtomicInteger;

import static com.netease.meetinglib.demo.SdkAuthenticator.AuthStateChangeListener.AUTHORIZED;
import static com.netease.meetinglib.demo.SdkAuthenticator.AuthStateChangeListener.AUTHORIZING;
import static com.netease.meetinglib.demo.SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE;

/**
 * 负责管理会议SDK的登录、登出
 */
public class SdkAuthenticator implements SdkInitializer.InitializeListener{

    private static final String TAG = "SdkAuthenticator";

    private static final SdkAuthenticator INSTANCE = new SdkAuthenticator();

    public static SdkAuthenticator getInstance() {
        return INSTANCE;
    }

    private static final String SP_FILE = "sdk-auth-cache";
    private static final String KEY_ACCOUNT = "ACCOUNT";
    private static final String KEY_PWD = "PWD";

    private Context context;
    private SharedPreferences cache;
    private AtomicInteger state = new AtomicInteger(UN_AUTHORIZE);
    private AuthStateChangeListener authStateChangeListener;
    private DataRepository dataRepository = new DefaultDataRepository();

    private SdkAuthenticator() {}

    public void initialize(Context context) {
        this.context = context;
        cache = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SdkInitializer.getInstance().addListener(this);
    }

    @Override
    public void onInitialized(int total) {
        if (total == 1) {
            loginWithCachedAccount();
        }
        NEMeetingSDK.getInstance().addAuthListener(new NEAuthListener() {
            @Override
            public void onKickOut() {
                onKickedOut();
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

    private void loginWithCachedAccount() {
        final String account = cache.getString(KEY_ACCOUNT, "");
        final String pwd = cache.getString(KEY_PWD, "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
            login(account, pwd);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void login(final String account, final String pwd) {
        if (state.compareAndSet(UN_AUTHORIZE, AUTHORIZING)) {
            notifyStateChanged();
            new AsyncTask<Void,Void, Response<SDKAuthInfo>>() {

                @Override
                protected Response<SDKAuthInfo> doInBackground(Void... voids) {
                    return dataRepository.getSDKAuthInfo(account, pwd);
                }

                @Override
                protected void onPostExecute(Response<SDKAuthInfo> response) {
                    handleResponse(account, pwd, response);
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (state.get() == AUTHORIZED){
            Toast.makeText(context, "您已登录", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(boolean manual) {
        if (state.get() == AUTHORIZED) {
            NEMeetingSDK.getInstance().logout(new ToastCallback<Void>(context, "注销"){
                @Override
                public void onResult(int resultCode, String resultMsg, Void resultData) {
                    //手动退出登录才进行toast提示
                    if (manual) super.onResult(resultCode, resultMsg, resultData);
                    if (state.compareAndSet(AUTHORIZED, UN_AUTHORIZE) && resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                        cache.edit().putString(KEY_ACCOUNT, "").putString(KEY_PWD, "").apply();
                        notifyStateChanged();
                    }
                }
            });
        }
    }

    private void handleResponse(String account, String pwd, Response<SDKAuthInfo> result) {
        if (result != null && result.code == 200 && result.data != null ) {
            NEMeetingSDK.getInstance().login(result.data.account, result.data.token,
                    new ToastCallback<Void>(context, "登录") {
                        @Override
                        public void onResult(int resultCode, String resultMsg, Void resultData) {
                            super.onResult(resultCode, resultMsg, resultData);
                            if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                                state.set(AUTHORIZED);
                                cache.edit().putString(KEY_ACCOUNT, account).putString(KEY_PWD, pwd).apply();
                            } else {
                                state.set(UN_AUTHORIZE);
                            }
                            notifyStateChanged();
                        }
                    });
        } else {
            state.set(UN_AUTHORIZE);
            notifyStateChanged();
            Toast.makeText(context,
                    result != null && !TextUtils.isEmpty(result.msg) ? result.msg : "登录失败，请检查网络连接后重试！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onKickedOut() {
        Log.i(TAG, "onKickOut");
        Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
        SdkAuthenticator.getInstance().logout(false);
    }

    public interface AuthStateChangeListener {

        /**
         * 未授权
         */
        int UN_AUTHORIZE = -1;

        /**
         * 授权中
         */
        int AUTHORIZING = 0;

        /**
         * 已授权
         */
        int AUTHORIZED = 1;

        void onAuthStateChanged(int state);
    }

}
