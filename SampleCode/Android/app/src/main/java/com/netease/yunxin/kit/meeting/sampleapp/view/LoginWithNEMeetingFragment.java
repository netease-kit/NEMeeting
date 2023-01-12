// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.TextWatcherAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentLoginWithNeMeetingBinding;

public class LoginWithNEMeetingFragment extends BaseFragment<FragmentLoginWithNeMeetingBinding> {
  private String account;
  private String pwd;

  @Override
  protected void initView() {
    binding.btnLogin.setOnClickListener(
        view -> {
          if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(), "账号或密码错误", Toast.LENGTH_SHORT).show();
            return;
          }
          SdkAuthenticator.getInstance().loginWithNEMeeting(account, pwd);
        });
    binding.edtLoginAccount.addTextChangedListener(
        new TextWatcherAdapter() {
          @Override
          public void afterTextChanged(Editable editable) {
            if (editable != null) {
              account = editable.toString();
            }
          }
        });
    binding.edtLoginPwd.addTextChangedListener(
        new TextWatcherAdapter() {

          @Override
          public void afterTextChanged(Editable editable) {
            if (editable != null) {
              pwd = editable.toString();
            }
          }
        });
  }

  @Override
  protected void initData() {}

  @Override
  protected FragmentLoginWithNeMeetingBinding getViewBinding() {
    return FragmentLoginWithNeMeetingBinding.inflate(getLayoutInflater());
  }
}
