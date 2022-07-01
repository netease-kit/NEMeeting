// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.ToastCallback;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingConfigRepository;
import com.netease.yunxin.kit.meeting.sampleapp.menu.InjectMenuArrangeActivity;
import com.netease.yunxin.kit.meeting.sampleapp.menu.InjectMenuContainer;
import com.netease.yunxin.kit.meeting.sampleapp.utils.AlertDialogUtil;
import com.netease.yunxin.kit.meeting.sdk.NEHistoryMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingCode;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingIdDisplayOption;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatusListener;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEWindowMode;
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioProfile;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public abstract class MeetingCommonFragment extends CommonFragment {

    MeetingCommonFragment() {
        super(R.layout.fragment_meeting_base);
    }

    private final int[] checkBoxIds = new int[]{
            R.id.videoOption, R.id.audioOption, R.id.noChatOptions,
            R.id.noInviteOptions, R.id.no_minimize, R.id.show_meeting_time,
            R.id.showLongMeetingIdOnly, R.id.showShortMeetingIdOnly, R.id.noGalleryOptions,
            R.id.noSwitchCamera, R.id.noSwitchAudioMode, R.id.noWhiteBoard, R.id.defaultWhiteBoard, R.id.noRename,
            R.id.noCloudRecord, R.id.showMemberTag,R.id.audioOffAllowSelfOn, R.id.audioOffNotAllowSelfOn,
            R.id.videoOffAllowSelfOn, R.id.videoOffNotAllowSelfOn,R.id.noMuteAllVideo,R.id.noMuteAllAudio
    };

    protected CheckBox usePersonalMeetingId;
    private final CheckBox[] checkBoxes = new CheckBox[checkBoxIds.length];
    private CheckBox useDefaultMeetingOptions;

    private EditText injectedMenuIdEdx, injectedMenuTitleEdx;

    private List<NEMeetingMenuItem> toolbarMenu;

    ActivityResultLauncher<Intent> configToolbarMenuResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    toolbarMenu = InjectMenuContainer.getSelectedMenu();
                }
            });

    private List<NEMeetingMenuItem> moreMenu;

    ActivityResultLauncher<Intent> configMoreMenuResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    moreMenu = InjectMenuContainer.getSelectedMenu();
                }
            });

    protected abstract String[] getEditorLabel();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        for (int index = 0; index < checkBoxIds.length; index++) {
            checkBoxes[index] = view.findViewById(checkBoxIds[index]);
        }

        usePersonalMeetingId = view.findViewById(R.id.usePersonalMeetingId);
        useDefaultMeetingOptions = view.findViewById(R.id.useDefaultOptions);

        String[] labels = getEditorLabel();

        addEditorArray(0, R.id.firstEditor, labels);
        addEditorArray(1, R.id.secondEditor, labels);
        addEditorArray(2, R.id.thirdEditor, labels);
        addEditorArray(3, R.id.fourthEditor, labels);
        addEditorArray(4, R.id.fifthEditor, labels);
        addEditorArray(5,R.id.roleBind,labels);
        Button configToolbarMenu = getView().findViewById(R.id.configToolbarMenus);
        configToolbarMenu.setOnClickListener(v -> {
            InjectMenuContainer.setSelectedMenu(toolbarMenu);
            configToolbarMenuResult.launch(new Intent(getActivity(), InjectMenuArrangeActivity.class));
        });
        Button configMoreMenu = getView().findViewById(R.id.configMoreMenus);
        configMoreMenu.setOnClickListener(v -> {
            InjectMenuContainer.setSelectedMenu(moreMenu);
            configMoreMenuResult.launch(new Intent(getActivity(), InjectMenuArrangeActivity.class));
        });
        useDefaultMeetingOptions.setChecked(false);
        useDefaultMeetingOptions.setOnCheckedChangeListener((checkbox, checked) -> {
            checkBoxes[0].setEnabled(!checked);
            checkBoxes[0].setChecked(false);
            checkBoxes[1].setEnabled(!checked);
            checkBoxes[1].setChecked(false);
            checkBoxes[5].setEnabled(!checked);
            checkBoxes[5].setChecked(false);
        });
        if (NEMeetingKit.getInstance().getMeetingService() != null) {
            NEMeetingKit.getInstance().getMeetingService().addMeetingStatusListener(listener);
        }
        groupCheckBoxesById(R.id.audioOffAllowSelfOn, R.id.audioOffNotAllowSelfOn);
        groupCheckBoxesById(R.id.videoOffAllowSelfOn, R.id.videoOffNotAllowSelfOn);
    }

    void groupCheckBoxesById(int... checkBoxIds) {
        Set<CheckBox> checkBoxes = new HashSet<>();
        for (int index = 0; index < checkBoxIds.length; index++) {
            checkBoxes.add(getView().findViewById(checkBoxIds[index]));
        }
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
            options.noVideo = !isChecked(0);
            options.noAudio = !isChecked(1);
            options.showMeetingTime = isChecked(5);
        } else {
            NESettingsService settingsService = NEMeetingKit.getInstance().getSettingsService();
            options.noVideo = !settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();
            options.noAudio = !settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();
            options.showMeetingTime = settingsService.isShowMyMeetingElapseTimeEnabled();
        }
        options.joinTimeout = MeetingConfigRepository.INSTANCE.getJoinTimeout();
        options.noChat = isChecked(2);
        options.noInvite = isChecked(3);
        options.noMinimize = isChecked(4);
        options.meetingIdDisplayOption = getMeetingIdDisplayOption();
        options.noGallery = isChecked(8);
        options.noSwitchCamera = isChecked(9);
        options.noSwitchAudioMode = isChecked(10);
        options.noWhiteBoard = isChecked(11);
        options.noSip = isCheckedById(R.id.noSip);
        options.defaultWindowMode = isChecked(12)? NEWindowMode.whiteBoard : NEWindowMode.normal;
        options.noRename = isCheckedById(R.id.noRename);
        options.showMemberTag = isCheckedById(R.id.showMemberTag);
        if (MeetingConfigRepository.INSTANCE.isMusicAudioMode()) {
            options.audioProfile = NEAudioProfile.createMusicAudioProfile();
        } else if (MeetingConfigRepository.INSTANCE.isSpeechAudioMode()) {
            options.audioProfile = NEAudioProfile.createSpeechAudioProfile();
        }
        // 如果是创建会议判断是否需要录制
        if (options instanceof NEStartMeetingOptions){
            ((NEStartMeetingOptions) options).noCloudRecord = !isCheckedById(R.id.noCloudRecord);
        }
        options.fullToolbarMenuItems = toolbarMenu;
        options.fullMoreMenuItems = moreMenu;
        options.noMuteAllAudio = isChecked(21);
        options.noMuteAllVideo = isChecked(20);
        return options;
    }

    private NEMeetingIdDisplayOption getMeetingIdDisplayOption() {
        if (isChecked(6)) return NEMeetingIdDisplayOption.DISPLAY_LONG_ID_ONLY;
        if (isChecked(7)) return NEMeetingIdDisplayOption.DISPLAY_SHORT_ID_ONLY;
        return NEMeetingIdDisplayOption.DISPLAY_ALL;
    }

    protected final boolean isNotUseDefaultMeetingOptions() {
        return !useDefaultMeetingOptions.isChecked();
    }

    public void clear() {

    }

    protected final boolean isChecked(int index) {
        return checkBoxes[index].isChecked();
    }

    protected final boolean isCheckedById(@IdRes int id) {
        return ((CheckBox)getView().findViewById(id)).isChecked();
    }

    protected class MeetingCallback extends ToastCallback<Void> {

        public MeetingCallback() {
            super(getActivity(), getActionLabel());
        }

        @Override
        public void onResult(int resultCode, String resultMsg,
                             Void resultData) {
            if (isAdded()) dissMissDialogProgress();
            if (resultCode == NEMeetingError.ERROR_CODE_NO_AUTH) {
                Toast.makeText(context, "当前账号已在其他设备上登录", Toast.LENGTH_SHORT).show();
                SdkAuthenticator.getInstance().logout(false);
            } else if (resultCode == NEMeetingError.ERROR_CODE_FAILED) {
                Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
                dissMissDialogProgress();
            } else if(resultCode == NEMeetingError.ERROR_CODE_MEETING_ALREADY_EXIST){
                Toast.makeText(context,"会议创建失败，该会议还在进行中",Toast.LENGTH_SHORT).show();
                dissMissDialogProgress();
            }else {
                super.onResult(resultCode, resultMsg, resultData);
            }
        }
    }


    private NEMeetingStatusListener listener = event -> {
        if (event.status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
            clear();
            // 增加会议断开连接提示。
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                    "会议已断开连接: " + stringifyDisconnectReason(event.arg), Toast.LENGTH_SHORT).show());
            if (AlertDialogUtil.getAlertDialog() != null) {
                AlertDialogUtil.getAlertDialog().dismiss();
            }

            new Handler().postDelayed(() -> {
                NEMeetingKit.getInstance().getSettingsService().getHistoryMeetingItem((resultCode, resultMsg, resultData) -> {
                    if (resultData != null && resultData.size() > 0) {
                        NEHistoryMeetingItem history = resultData.get(0);
                        Log.d("MeetingCommonFragment", "getHistoryMeetingItem: " + history);
                        if (history.getMeetingId().equals(getFirstEditTextContent())) {
                            getEditor(1).setText(history.getNickname());
                        }
                    }
                });
            }, 1500);
        }
        if (event.status != NEMeetingStatus.MEETING_STATUS_WAITING) {
            dissMissDialogProgress();//输入密码等待中
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        NEMeetingKit.getInstance().getMeetingService().removeMeetingStatusListener(listener);
    }

    static String stringifyDisconnectReason(int reason) {
        switch (reason) {
            case NEMeetingCode.MEETING_DISCONNECTING_BY_SELF: return "leave_by_self";
            case NEMeetingCode.MEETING_DISCONNECTING_REMOVED_BY_HOST: return "remove_by_host";
            case NEMeetingCode.MEETING_DISCONNECTING_CLOSED_BY_HOST: return "close_by_host";
            case NEMeetingCode.MEETING_DISCONNECTING_LOGIN_ON_OTHER_DEVICE: return "login_on_other_device";
            case NEMeetingCode.MEETING_DISCONNECTING_CLOSED_BY_SELF_AS_HOST: return "close_by_self";
            case NEMeetingCode.MEETING_DISCONNECTING_AUTH_INFO_EXPIRED: return "auth_info_expired";
            case NEMeetingCode.MEETING_DISCONNECTING_NOT_EXIST: return "meeting_not_exist";
            case NEMeetingCode.MEETING_DISCONNECTING_SYNC_DATA_ERROR: return "sync_data_error";
            case NEMeetingCode.MEETING_DISCONNECTING_RTC_INIT_ERROR: return "rtc_init_error";
            case NEMeetingCode.MEETING_DISCONNECTING_JOIN_CHANNEL_ERROR: return "join_channel_error";
            case NEMeetingCode.MEETING_DISCONNECTING_JOIN_TIMEOUT: return "join_timeout";
            case NEMeetingCode.MEETING_DISCONNECTING_END_OF_LIFE: return "meeting end of life";
        }
        return "unknown";
    }
}
