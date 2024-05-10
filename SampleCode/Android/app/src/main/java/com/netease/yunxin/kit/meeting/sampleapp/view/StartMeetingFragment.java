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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.StartMeetingViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEAccountService;
import com.netease.yunxin.kit.meeting.sdk.NEEncryptionConfig;
import com.netease.yunxin.kit.meeting.sdk.NEEncryptionMode;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAttendeeOffType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAudioControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingRoleType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVideoControl;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingParams;
import com.netease.yunxin.kit.meeting.sdk.NEWatermarkConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class StartMeetingFragment extends MeetingCommonFragment {
  private String meetingNum;
  private String currentMeetingNum;
  private String content;
  private String tag;
  private JSONObject jsonScene;
  private StartMeetingViewModel mViewModel;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    binding.etMeetingNum.setHint("会议号(留空或使用个人会议号)");
    binding.etExtra.setHint("扩展字段");
    binding.etRoleBind.setHint("json结构uid-role:{\"dew323esd23ew23e3r\":1}");
    binding.usePersonalMeetingNum.setEnabled(true);
    binding.usePersonalMeetingNum.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          determineMeetingNum();
        });
    mViewModel = ViewModelProviders.of(this).get(StartMeetingViewModel.class);
    initData();
    return binding.getRoot();
  }

  /** 初始化数据，为了方便测试，可通过ADB传递参数进行验证 当前支持传入tag和scene tag:String类型 scene:Json类型 */
  private void initData() {
    Intent intent = getActivity().getIntent();
    tag = intent.getStringExtra("tag");
    String strScene = intent.getStringExtra("scene");
    if (!TextUtils.isEmpty(strScene)) {
      try {
        jsonScene = new JSONObject(strScene);
      } catch (Exception e) {
        Toast.makeText(this.getContext(), "sence params is error", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  protected String getActionLabel() {
    return "创建会议";
  }

  @Override
  protected void performAction(
      String meetingNum, String displayName, String password, String personalTag) {
    NEStartMeetingParams params = new NEStartMeetingParams();
    params.meetingNum =
        binding.usePersonalMeetingNum.isChecked() && !TextUtils.isEmpty(currentMeetingNum)
            ? currentMeetingNum
            : meetingNum;
    params.displayName = displayName;
    if (!TextUtils.isEmpty(password)) {
      params.password = password;
    }
    if (!TextUtils.isEmpty(tag)) {
      params.tag = tag;
    }
    if (!TextUtils.isEmpty(personalTag)) {
      params.tag = personalTag;
    }
    String extraData = binding.etExtra.getText().toString();
    if (!TextUtils.isEmpty(extraData)) {
      params.extraData = extraData;
    }
    String roleBindsStr = binding.etRoleBind.getText().toString();
    if (!TextUtils.isEmpty(roleBindsStr)) {
      try {
        JSONObject roleBindJson = new JSONObject(roleBindsStr);
        Map<String, NEMeetingRoleType> roleBinds = new HashMap<>();
        Iterator<String> iterator = roleBindJson.keys();
        while (iterator.hasNext()) {
          String key = iterator.next();
          NEMeetingRoleType roleType = NEMeetingRoleType.values()[roleBindJson.optInt(key)];
          roleBinds.put(key, roleType);
        }
        params.roleBinds = roleBinds;
      } catch (JSONException e) {
        Toast.makeText(getContext(), "绑定角色数据结构出错了", Toast.LENGTH_SHORT).show();
        return;
      }
    }

    List<NEMeetingControl> controls = new ArrayList<>();
    if (binding.audioOffAllowSelfOn.isChecked()) {
      controls.add(new NEMeetingAudioControl(NEMeetingAttendeeOffType.OffAllowSelfOn));
    } else if (binding.audioOffNotAllowSelfOn.isChecked()) {
      controls.add(new NEMeetingAudioControl(NEMeetingAttendeeOffType.OffNotAllowSelfOn));
    }
    if (binding.videoOffAllowSelfOn.isChecked()) {
      controls.add(new NEMeetingVideoControl(NEMeetingAttendeeOffType.OffAllowSelfOn));
    } else if (binding.videoOffNotAllowSelfOn.isChecked()) {
      controls.add(new NEMeetingVideoControl(NEMeetingAttendeeOffType.OffNotAllowSelfOn));
    }
    if (controls.size() > 0) {
      params.controls = controls;
    }
    if (binding.cbEncryption.isChecked()) {
      params.encryptionConfig =
          new NEEncryptionConfig(
              NEEncryptionMode.GMCryptoSM4ECB, binding.etEncryption.getText().toString());
    }
    params.watermarkConfig =
        new NEWatermarkConfig(SdkAuthenticator.getAccount(displayName), null, null, null);
    NEStartMeetingOptions options =
        (NEStartMeetingOptions) getMeetingOptions(new NEStartMeetingOptions());
    options.enableWaitingRoom = binding.enableWaitingRoom.isChecked();
    options.enableAudioDeviceSwitch = binding.enableAudioDeviceSwitch.isChecked();
    options.enableGuestJoin = binding.enableGuestJoin.isChecked();
    showDialogProgress("正在创建会议...");
    mViewModel.startMeeting(params, options, new MeetingCallback());
  }

  private void determineMeetingNum() {
    if (binding.usePersonalMeetingNum.isChecked()) {
      currentMeetingNum = meetingNum;
      if (!TextUtils.isEmpty(content)) {
        binding.etMeetingNum.setText(content);
        return;
      }
      NEAccountService accountService = NEMeetingKit.getInstance().getAccountService();
      if (accountService == null) {
        onGetPersonalMeetingNumError();
      } else {
        accountService.getAccountInfo(
            (resultCode, resultMsg, resultData) -> {
              if (!isAdded()) return;
              if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS && resultData != null) {
                meetingNum = resultData.meetingNum;
                currentMeetingNum = meetingNum;
                content =
                    meetingNum
                        + (!TextUtils.isEmpty(resultData.shortMeetingNum)
                            ? "(短号：" + resultData.shortMeetingNum + ")"
                            : "");
                binding.etMeetingNum.setText(content);
              } else {
                onGetPersonalMeetingNumError();
              }
            });
      }
    } else {
      currentMeetingNum = null;
      binding.etMeetingNum.setText("");
    }
  }

  private void onGetPersonalMeetingNumError() {
    content = null;
    currentMeetingNum = null;
    binding.usePersonalMeetingNum.setChecked(false);
    Toast.makeText(getActivity(), "获取个人会议号失败", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
