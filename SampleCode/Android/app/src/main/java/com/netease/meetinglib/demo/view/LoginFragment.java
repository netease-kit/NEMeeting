/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;


import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.adapter.TextWatcherAdapter;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.databinding.FragmentLoginBinding;


public class LoginFragment extends BaseFragment<FragmentLoginBinding> {
    private String account;
    private String pwd;

    @Override
    protected void initView() {
        binding.btnLogin.setOnClickListener(view -> {
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(getActivity(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
            SdkAuthenticator.getInstance().login(account, pwd);
        });
        binding.edtLoginAccount.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    account = editable.toString();
                }
            }
        });
        binding.edtLoginPwd.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    pwd = editable.toString();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected FragmentLoginBinding getViewBinding() {
        return FragmentLoginBinding.inflate(getLayoutInflater());
    }
}
