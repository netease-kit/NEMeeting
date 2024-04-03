// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.netease.yunxin.kit.meeting.sampleapp.MeetingSettingsActivity;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.ToastCallback;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingConfigRepository;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentMeetingBaseBinding;
import com.netease.yunxin.kit.meeting.sampleapp.menu.InjectMenuArrangeActivity;
import com.netease.yunxin.kit.meeting.sampleapp.menu.InjectMenuContainer;
import com.netease.yunxin.kit.meeting.sampleapp.utils.AlertDialogUtil;
import com.netease.yunxin.kit.meeting.sdk.NEHistoryMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingChatroomConfig;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingCode;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingIdDisplayOption;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatusListener;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEWindowMode;
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioProfile;
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioProfileType;
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioScenarioType;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;
import java.util.List;

public abstract class MeetingCommonFragment extends CommonFragment {

  FragmentMeetingBaseBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    binding = FragmentMeetingBaseBinding.inflate(inflater, container, false);
    initView();
    return binding.getRoot();
  }

  protected abstract String getActionLabel();

  private List<NEMeetingMenuItem> toolbarMenu;

  ActivityResultLauncher<Intent> configToolbarMenuResult =
      registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
              toolbarMenu = InjectMenuContainer.getSelectedMenu();
            }
          });

  private List<NEMeetingMenuItem> moreMenu;

  ActivityResultLauncher<Intent> configMoreMenuResult =
      registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
              moreMenu = InjectMenuContainer.getSelectedMenu();
            }
          });

  void initView() {
    binding.etNickname.setHint("昵称");
    binding.etPassword.setHint("请输入密码");
    binding.etPersonalTag.setHint("个人TAG");
    binding.etEncryption.setHint("媒体流加密密钥");
    binding.etNickname.setSingleLine();
    binding.etPassword.setSingleLine();
    binding.etPersonalTag.setSingleLine();
    binding.etEncryption.setSingleLine();
    binding.etRoleBind.setSingleLine();
    binding.etExtra.setSingleLine();
    binding.etMeetingNum.setSingleLine();
    binding.title.setText(getActionLabel());
    binding.actionBtn.setText(getActionLabel());
    binding.actionBtn.setOnClickListener(
        v ->
            performAction(
                binding.etMeetingNum.getText().toString(),
                binding.etNickname.getText().toString(),
                binding.etPassword.getText().toString(),
                binding.etPersonalTag.getText().toString()));

    binding.actionToMeetingSettings.setOnClickListener(
        v -> MeetingSettingsActivity.start(getActivity()));
    binding.configToolbarMenus.setOnClickListener(
        v -> {
          InjectMenuContainer.setSelectedMenu(toolbarMenu);
          configToolbarMenuResult.launch(
              new Intent(getActivity(), InjectMenuArrangeActivity.class));
        });
    binding.configMoreMenus.setOnClickListener(
        v -> {
          InjectMenuContainer.setSelectedMenu(moreMenu);
          configMoreMenuResult.launch(new Intent(getActivity(), InjectMenuArrangeActivity.class));
        });
    binding.useDefaultOptions.setChecked(false);
    binding.showCloudRecordMenuItem.setChecked(true);
    binding.showCloudRecordingUI.setChecked(true);
    binding.useDefaultOptions.setOnCheckedChangeListener(
        (checkbox, checked) -> {
          binding.videoOption.setEnabled(!checked);
          binding.videoOption.setChecked(false);
          binding.audioOption.setEnabled(!checked);
          binding.audioOption.setChecked(false);
          binding.showMeetingTime.setEnabled(!checked);
          binding.showMeetingTime.setChecked(false);
        });
    if (NEMeetingKit.getInstance().getMeetingService() != null) {
      NEMeetingKit.getInstance().getMeetingService().addMeetingStatusListener(listener);
    }
    groupCheckBoxesById(binding.audioOffAllowSelfOn, binding.audioOffNotAllowSelfOn);
    groupCheckBoxesById(binding.videoOffAllowSelfOn, binding.videoOffNotAllowSelfOn);
  }

  void groupCheckBoxesById(CheckBox... checkBoxes) {
    for (CheckBox checkBox : checkBoxes) {
      checkBox.setOnCheckedChangeListener(
          (buttonView, isChecked) -> {
            if (isChecked) {
              for (CheckBox box : checkBoxes) {
                if (box != checkBox) {
                  box.setChecked(false);
                }
              }
            }
          });
    }
  }

  public NEMeetingOptions getMeetingOptions(NEMeetingOptions options) {
    if (isNotUseDefaultMeetingOptions()) {
      options.noVideo = !binding.videoOption.isChecked();
      options.noAudio = !binding.audioOption.isChecked();
      options.showMeetingTime = binding.showMeetingTime.isChecked();
    } else {
      NESettingsService settingsService = NEMeetingKit.getInstance().getSettingsService();
      options.noVideo = !settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();
      options.noAudio = !settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();
      options.showMeetingTime = settingsService.isShowMyMeetingElapseTimeEnabled();
    }
    options.joinTimeout = MeetingConfigRepository.INSTANCE.getJoinTimeout();

    options.noChat = binding.noChatOptions.isChecked();
    boolean disableImageMessage = binding.disableImageMessage.isChecked();
    boolean disableFileMessage = binding.disableFileMessage.isChecked();
    if (disableImageMessage || disableFileMessage) {
      NEMeetingChatroomConfig chatroomConfig = new NEMeetingChatroomConfig();
      chatroomConfig.enableImageMessage = !disableImageMessage;
      chatroomConfig.enableFileMessage = !disableFileMessage;
      options.chatroomConfig = chatroomConfig;
    }

    options.noInvite = binding.noInviteOptions.isChecked();
    options.noMinimize = binding.noMinimize.isChecked();
    options.meetingIdDisplayOption = getMeetingIdDisplayOption();
    options.noGallery = binding.noGalleryOptions.isChecked();
    options.noSwitchCamera = binding.noSwitchCamera.isChecked();
    options.enableFrontCameraMirror = binding.enableFrontCameraMirror.isChecked();
    options.noSwitchAudioMode = binding.noSwitchAudioMode.isChecked();
    options.noWhiteBoard = binding.noWhiteBoard.isChecked();
    options.noSip = binding.noSip.isChecked();
    options.defaultWindowMode =
        binding.defaultWhiteBoard.isChecked() ? NEWindowMode.whiteBoard : NEWindowMode.normal;
    options.noRename = binding.noRename.isChecked();
    options.noLive = binding.noLive.isChecked();
    options.showMemberTag = binding.showMemberTag.isChecked();
    options.showMeetingRemainingTip = binding.showMeetingRemainingTip.isChecked();
    if (MeetingConfigRepository.INSTANCE.getEnableAudioOptions()) {
      options.audioProfile =
          new NEAudioProfile(
              parseIntCatching(
                  MeetingConfigRepository.INSTANCE.getAudioProfile(),
                  NEAudioProfileType.UNSPECIFIED),
              parseIntCatching(
                  MeetingConfigRepository.INSTANCE.getAudioScenario(),
                  NEAudioScenarioType.UNSPECIFIED),
              NEMeetingKit.getInstance().getSettingsService().isAudioAINSEnabled());
    }
    // 如果是创建会议判断是否需要录制
    if (options instanceof NEStartMeetingOptions) {
      ((NEStartMeetingOptions) options).noCloudRecord = !binding.cloudRecord.isChecked();
    }
    options.fullToolbarMenuItems = toolbarMenu;
    options.fullMoreMenuItems = moreMenu;
    options.noMuteAllAudio = binding.noMuteAllAudio.isChecked();
    options.noMuteAllVideo = binding.noMuteAllVideo.isChecked();
    options.detectMutedMic = binding.detectMutedMic.isChecked();
    options.unpubAudioOnMute = binding.unpubAudioOnMute.isChecked();
    options.showScreenShareUserVideo = binding.showScreenShareUserVideo.isChecked();
    options.showWhiteboardShareUserVideo = binding.showWhiteboardShareUserVideo.isChecked();
    options.showFloatingMicrophone = binding.showFloatingMicrophone.isChecked();
    options.enableTransparentWhiteboard = binding.enableTransparentWhiteboard.isChecked();
    options.enableAudioShare = binding.enableAudioShare.isChecked();
    options.showCloudRecordingUI = binding.showCloudRecordingUI.isChecked();
    options.showCloudRecordMenuItem = binding.showCloudRecordMenuItem.isChecked();
    //    options.enablePictureInPicture = isCheckedById(R.id.enablePictureInPicture);
    return options;
  }

  private static int parseIntCatching(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  private NEMeetingIdDisplayOption getMeetingIdDisplayOption() {
    if (binding.showLongMeetingIdOnly.isChecked())
      return NEMeetingIdDisplayOption.DISPLAY_LONG_ID_ONLY;
    if (binding.showShortMeetingIdOnly.isChecked())
      return NEMeetingIdDisplayOption.DISPLAY_SHORT_ID_ONLY;
    return NEMeetingIdDisplayOption.DISPLAY_ALL;
  }

  protected final boolean isNotUseDefaultMeetingOptions() {
    return !binding.useDefaultOptions.isChecked();
  }

  public void clear() {}

  protected class MeetingCallback extends ToastCallback<Void> {

    public MeetingCallback() {
      super(getActivity(), getActionLabel());
    }

    @Override
    public void onResult(int resultCode, String resultMsg, Void resultData) {
      if (isAdded()) dissMissDialogProgress();
      if (resultCode == NEMeetingError.ERROR_CODE_NO_AUTH) {
        Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
        SdkAuthenticator.getInstance().logout(false);
      } else if (resultCode == NEMeetingError.ERROR_CODE_FAILED) {
        Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
        dissMissDialogProgress();
      } else if (resultCode == NEMeetingError.ERROR_CODE_MEETING_ALREADY_EXIST) {
        Toast.makeText(context, "会议创建失败，该会议还在进行中", Toast.LENGTH_SHORT).show();
        dissMissDialogProgress();
      } else if (resultCode != NEMeetingError.ERROR_CODE_SUCCESS) {
        super.onResult(resultCode, resultMsg, resultData);
      }
    }
  }

  private NEMeetingStatusListener listener =
      event -> {
        if (event.status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
          clear();
          // 增加会议断开连接提示。
          getActivity()
              .runOnUiThread(
                  () ->
                      Toast.makeText(
                              getActivity(),
                              "会议已断开连接: " + stringifyDisconnectReason(event.arg),
                              Toast.LENGTH_SHORT)
                          .show());
          if (AlertDialogUtil.getAlertDialog() != null) {
            AlertDialogUtil.getAlertDialog().dismiss();
          }

          new Handler()
              .postDelayed(
                  () -> {
                    NEMeetingKit.getInstance()
                        .getSettingsService()
                        .getHistoryMeetingItem(
                            (resultCode, resultMsg, resultData) -> {
                              if (resultData != null && resultData.size() > 0) {
                                NEHistoryMeetingItem history = resultData.get(0);
                                Log.d("MeetingCommonFragment", "getHistoryMeetingItem: " + history);
                                if (history
                                    .getMeetingNum()
                                    .equals(binding.etMeetingNum.getText().toString())) {
                                  binding.etNickname.setText(history.getNickname());
                                }
                              }
                            });
                  },
                  1500);
        }
        if (event.status != NEMeetingStatus.MEETING_STATUS_WAITING) {
          dissMissDialogProgress(); //输入密码等待中
        }
      };

  @Override
  public void onDestroy() {
    super.onDestroy();
    NEMeetingKit.getInstance().getMeetingService().removeMeetingStatusListener(listener);
  }

  static String stringifyDisconnectReason(int reason) {
    switch (reason) {
      case NEMeetingCode.MEETING_DISCONNECTING_BY_SELF:
        return "leave_by_self";
      case NEMeetingCode.MEETING_DISCONNECTING_REMOVED_BY_HOST:
        return "remove_by_host";
      case NEMeetingCode.MEETING_DISCONNECTING_CLOSED_BY_HOST:
        return "close_by_host";
      case NEMeetingCode.MEETING_DISCONNECTING_LOGIN_ON_OTHER_DEVICE:
        return "login_on_other_device";
      case NEMeetingCode.MEETING_DISCONNECTING_CLOSED_BY_SELF_AS_HOST:
        return "close_by_self";
      case NEMeetingCode.MEETING_DISCONNECTING_AUTH_INFO_EXPIRED:
        return "auth_info_expired";
      case NEMeetingCode.MEETING_DISCONNECTING_NOT_EXIST:
        return "meeting_not_exist";
      case NEMeetingCode.MEETING_DISCONNECTING_SYNC_DATA_ERROR:
        return "sync_data_error";
      case NEMeetingCode.MEETING_DISCONNECTING_RTC_INIT_ERROR:
        return "rtc_init_error";
      case NEMeetingCode.MEETING_DISCONNECTING_JOIN_CHANNEL_ERROR:
        return "join_channel_error";
      case NEMeetingCode.MEETING_DISCONNECTING_JOIN_TIMEOUT:
        return "join_timeout";
      case NEMeetingCode.MEETING_DISCONNECTING_END_OF_LIFE:
        return "meeting end of life";
    }
    return "unknown";
  }
}
