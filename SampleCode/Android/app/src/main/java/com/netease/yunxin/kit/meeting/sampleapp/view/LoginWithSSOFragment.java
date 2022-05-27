/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.TextWatcherAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentLoginWithSsoBinding;


public class LoginWithSSOFragment extends BaseFragment<FragmentLoginWithSsoBinding> {
    private String account;
    private String pwd;

    @Override
    protected void initView() {
        binding.btnLogin.setOnClickListener(view -> {
            if (TextUtils.isEmpty(account)) {
                Toast.makeText(getActivity(), "请输入企业代码", Toast.LENGTH_SHORT).show();
                return;
            }

            String ssoAppNamespace = binding.edtLoginCorpCode.getText().toString().trim();
            String ssoClientLoginUrl = "nemeetingdemo://meeting.netease.im/";
            String targetUrl = getBaseUrl() + "/v1/auth/sso/authorize?ssoAppNamespace=" + ssoAppNamespace + "&ssoClientLoginUrl=" + Uri.encode(ssoClientLoginUrl);
            Uri uri = Uri.parse(targetUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        binding.edtLoginCorpCode.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    account = editable.toString();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected FragmentLoginWithSsoBinding getViewBinding() {
        return FragmentLoginWithSsoBinding.inflate(getLayoutInflater());
    }

    private String getBaseUrl() {
        return MeetingApplication.getInstance().getServerConfig().getAppServerUrl();
    }
}
