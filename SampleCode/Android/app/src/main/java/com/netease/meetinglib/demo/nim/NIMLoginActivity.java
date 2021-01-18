/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.nim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.netease.meetinglib.demo.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

public class NIMLoginActivity extends AppCompatActivity implements Observer<StatusCode> {

    private static final String TAG = "IMLoginActivity";

    private static final String NIM_APPKEY = "your_nim_appkey";

    public static void start(Context context) {
        Intent starter = new Intent(context, NIMLoginActivity.class);
        context.startActivity(starter);
    }

    EditText accountEdx, pwdEdx;
    SwitchCompat imReuseSwitch;
    TextView loginState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_login);

        accountEdx = findViewById(R.id.firstEditor);
        pwdEdx = findViewById(R.id.secondEditor);

        imReuseSwitch = findViewById(R.id.toggleIMReuse);
        imReuseSwitch.setChecked(NIMInitializer.getInstance().isReuseNIMEnabled());

        loginState = findViewById(R.id.imLoginState);
        refreshOnlineStatus();

        findViewById(R.id.loginBtn).setOnClickListener( v -> login());
        findViewById(R.id.logoutBtn).setOnClickListener( v -> logout());
    }

    private void login() {
        final String account = accountEdx.getText().toString();
        final String token = pwdEdx.getText().toString();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            NIMClient.getService(AuthService.class).login(new LoginInfo(account, token, NIM_APPKEY));
        }
    }

    private void logout() {
        NIMClient.getService(AuthService.class).logout();
        //延迟刷新
        accountEdx.postDelayed(this::refreshOnlineStatus, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(StatusCode statusCode) {
        Log.i(TAG, "onIMEvent: " + statusCode);
        refreshOnlineStatus();
    }

    private void refreshOnlineStatus() {
        String fromAccount = MessageBuilder.createTextMessage("", SessionTypeEnum.None, "").getFromAccount();
        loginState.setText("当前IM状态: " + fromAccount + '#' + NIMClient.getStatus());
    }
}