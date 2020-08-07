/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;


public class LoginFragment extends BaseFragment {

    public LoginFragment() {
        super(R.layout.fragment_base);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getEditor(1).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"账号","密码"};
    }

    @Override
    protected String getActionLabel() {
        return "登录";
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void performAction(final String first, final String second) {
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(second)) {
            Toast.makeText(getActivity(), "账号或密码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        SdkAuthenticator.getInstance().login(first, second);
    }
}
