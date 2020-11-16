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
import com.netease.meetinglib.demo.databinding.FragmentLoginWithTokenBinding;


public class LoginWithTokenFragment extends BaseFragment<FragmentLoginWithTokenBinding> {
    private String accountId;
    private String accountToken;
    private String account;
    private String pwd;

    @Override
    protected void initView() {
        binding.btnLoginWithAppToken.setOnClickListener(view -> {
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

        binding.btnLoginWithSdkToken.setOnClickListener(view -> {
            if (TextUtils.isEmpty(accountId) || TextUtils.isEmpty(accountToken)) {
                Toast.makeText(getActivity(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
            SdkAuthenticator.getInstance().loginWithSDKToken(accountId, accountToken);
        });

        binding.edtLoginAccountId.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    accountId = editable.toString();
                }
            }
        });
        binding.edtLoginAccountToken.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    accountToken = editable.toString();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected FragmentLoginWithTokenBinding getViewBinding() {
        return FragmentLoginWithTokenBinding.inflate(getLayoutInflater());
    }
}
