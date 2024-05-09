// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.JoinMeetingViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEEncryptionConfig;
import com.netease.yunxin.kit.meeting.sdk.NEEncryptionMode;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingParams;
import com.netease.yunxin.kit.meeting.sdk.NEWatermarkConfig;

public class JoinMeetingFragment extends MeetingCommonFragment {
  private JoinMeetingViewModel mViewModel;
  private String tag;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    binding.etMeetingNum.setHint("会议号");
    binding.usePersonalMeetingNum.setEnabled(false);
    // 加入会议隐藏录制开关功能
    binding.cloudRecord.setEnabled(false);
    binding.enableWaitingRoom.setEnabled(false);
    // 加入会议隐藏拓展字段和绑定角色
    binding.etExtra.setVisibility(View.GONE);
    binding.roleBindTips.setVisibility(View.GONE);
    binding.etRoleBind.setVisibility(View.GONE);
    mViewModel = ViewModelProviders.of(this).get(JoinMeetingViewModel.class);
    initData();
    return binding.getRoot();
  }

  /** 初始化数据，为了方便测试，可通过ADB传递参数进行验证 当前支持传入tag tag:String类型 */
  private void initData() {
    Intent intent = getActivity().getIntent();
    tag = intent.getStringExtra("tag");
  }

  protected String getActionLabel() {
    return isAnonymous() ? "匿名入会" : "加入会议";
  }

  @Override
  protected void performAction(
      String meetingNum, String displayName, String password, String personalTag) {
    NEJoinMeetingParams params = new NEJoinMeetingParams();
    params.meetingNum = meetingNum;
    params.displayName = displayName;
    params.password = password;
    if (!TextUtils.isEmpty(tag)) {
      params.tag = tag;
    }
    if (!TextUtils.isEmpty(personalTag)) {
      params.tag = personalTag;
    }
    if (binding.cbEncryption.isChecked()) {
      params.encryptionConfig =
          new NEEncryptionConfig(
              NEEncryptionMode.GMCryptoSM4ECB, binding.etEncryption.getText().toString());
    }
    params.watermarkConfig =
        new NEWatermarkConfig(SdkAuthenticator.getAccount(displayName), null, null, null);
    NEJoinMeetingOptions options =
        (NEJoinMeetingOptions) getMeetingOptions(new NEJoinMeetingOptions());
    options.enableAudioDeviceSwitch = binding.enableAudioDeviceSwitch.isChecked();
    showDialogProgress("正在加入会议...");
    if (isAnonymous()) {
      mViewModel.anonymousJoinMeeting(params, options, new MeetingCallback());
    } else {
      mViewModel.joinMeeting(params, options, new MeetingCallback());
    }
  }

  private boolean isAnonymous() {
    return !SdkAuthenticator.getInstance().isAuthorized();
  }
}
