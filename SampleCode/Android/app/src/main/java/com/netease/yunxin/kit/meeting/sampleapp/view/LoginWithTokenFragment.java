/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;


import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.TextWatcherAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentLoginWithTokenBinding;


public class LoginWithTokenFragment extends BaseFragment<FragmentLoginWithTokenBinding> {
    private String accountId;
    private String accountToken;
    private String account;
    private String pwd;

    @Override
    protected void initView() {

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
