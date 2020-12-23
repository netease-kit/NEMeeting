/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

public class EntryActivity  extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoLogin(getIntent().getDataString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ssoLogin(getIntent().getDataString());
    }

    private void ssoLogin(String deepLink){
        if(!TextUtils.isEmpty(deepLink)) {
            String ssoToken = Uri.parse(deepLink).getQueryParameter("ssoToken");
            if(!TextUtils.isEmpty(ssoToken)){
                SdkAuthenticator.getInstance().loginWithSSO(ssoToken);
            }
        }
        finish();
    }
}
