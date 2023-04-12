// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.nim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.R;

public class NIMLoginActivity extends AppCompatActivity {

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
    imReuseSwitch.setChecked(NIMAuthService.getInstance().isReuseNIMEnabled());

    loginState = findViewById(R.id.imLoginState);
    NIMAuthService.getInstance().getStatusLiveData().observe(this, this::refreshOnlineStatus);

    findViewById(R.id.loginBtn).setOnClickListener(v -> login());
    findViewById(R.id.logoutBtn).setOnClickListener(v -> NIMAuthService.getInstance().logout());
  }

  private void login() {
    final String account = accountEdx.getText().toString();
    final String token = pwdEdx.getText().toString();
    if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
      NIMAuthService.getInstance().login(getAppKey(), account, token);
    }
  }

  private void refreshOnlineStatus(StatusCode statusCode) {
    String fromAccount =
        MessageBuilder.createTextMessage("", SessionTypeEnum.None, "").getFromAccount();
    loginState.setText("当前IM状态: " + fromAccount + '#' + statusCode);
  }

  private static String getAppKey() {
    return MeetingApplication.getInstance().getServerConfig().getAppKey();
  }
}
