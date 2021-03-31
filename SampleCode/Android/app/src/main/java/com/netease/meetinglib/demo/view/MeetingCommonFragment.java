/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.demo.menu.InjectMenuArrangeActivity;
import com.netease.meetinglib.demo.menu.InjectMenuContainer;
import com.netease.meetinglib.demo.utils.AlertDialogUtil;
import com.netease.meetinglib.sdk.NEHistoryMeetingItem;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingIdDisplayOption;
import com.netease.meetinglib.sdk.NEMeetingOptions;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.netease.meetinglib.sdk.NEMeetingStatusListener;
import com.netease.meetinglib.sdk.NESettingsService;
import com.netease.meetinglib.sdk.NEWindowMode;
import com.netease.meetinglib.sdk.menu.NEMeetingMenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleKt;

public abstract class MeetingCommonFragment extends CommonFragment {

    MeetingCommonFragment() {
        super(R.layout.fragment_meeting_base);
    }

    private final int[] checkBoxIds = new int[]{
            R.id.videoOption, R.id.audioOption, R.id.noChatOptions,
            R.id.noInviteOptions, R.id.no_minimize, R.id.show_meeting_time,
            R.id.showLongMeetingIdOnly, R.id.showShortMeetingIdOnly, R.id.noGalleryOptions,
            R.id.noSwitchCamera, R.id.noSwitchAudioMode, R.id.noWhiteBoard, R.id.defaultWhiteBoard, R.id.noRename,
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

    List<com.netease.meetinglib.sdk.NEMeetingMenuItem> injectedMoreMenuItems;

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
        injectedMenuIdEdx = view.findViewById(R.id.injectedMenuIdEdx);
        injectedMenuTitleEdx = view.findViewById(R.id.injectedMenuTitleEdx);
        view.findViewById(R.id.addInjectedMenuItem).setOnClickListener(v -> addInjectedMenuItem());
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
        if (NEMeetingSDK.getInstance().getMeetingService() != null) {
            NEMeetingSDK.getInstance().getMeetingService().addMeetingStatusListener(listener);
        }
    }

    public NEMeetingOptions getMeetingOptions(NEMeetingOptions options) {
        if (isNotUseDefaultMeetingOptions()) {
            options.noVideo = !isChecked(0);
            options.noAudio = !isChecked(1);
            options.showMeetingTime = isChecked(5);
        } else {
            NESettingsService settingsService = NEMeetingSDK.getInstance().getSettingsService();
            options.noVideo = !settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();
            options.noAudio = !settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();
            options.showMeetingTime = settingsService.isShowMyMeetingElapseTimeEnabled();
        }
        options.noChat = isChecked(2);
        options.noInvite = isChecked(3);
        options.noMinimize = isChecked(4);
        options.meetingIdDisplayOption = getMeetingIdDisplayOption();
        options.noGallery = isChecked(8);
        options.noSwitchCamera = isChecked(9);
        options.noSwitchAudioMode = isChecked(10);
        options.noWhiteBoard = isChecked(11);
        options.defaultWindowMode = isChecked(12)? NEWindowMode.whiteBoard : NEWindowMode.normal;
        options.noRename = isCheckedById(R.id.noRename);
        options.fullToolbarMenuItems = toolbarMenu;
        options.fullMoreMenuItems = moreMenu;
        options.injectedMoreMenuItems = injectedMoreMenuItems;
        return options;
    }

    private void addInjectedMenuItem() {
        if (!TextUtils.isEmpty(injectedMenuIdEdx.getText()) && !TextUtils.isEmpty(injectedMenuTitleEdx.getText())) {
            if (injectedMoreMenuItems == null) {
                injectedMoreMenuItems = new ArrayList<>();
            }
            com.netease.meetinglib.sdk.NEMeetingMenuItem item = new com.netease.meetinglib.sdk.NEMeetingMenuItem();
            item.itemId = Integer.parseInt(injectedMenuIdEdx.getText().toString());
            item.title = injectedMenuTitleEdx.getText().toString();
            injectedMoreMenuItems.add(item);
        }
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
        if (injectedMoreMenuItems != null) {
            injectedMoreMenuItems.clear();
        }
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
            } else {
                super.onResult(resultCode, resultMsg, resultData);
            }
        }
    }


    private NEMeetingStatusListener listener = event -> {
        if (event.status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
            clear();
            // 增加会议断开连接提示。
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                    "会议已断开连接", Toast.LENGTH_SHORT).show());
            if (AlertDialogUtil.getAlertDialog() != null) {
                AlertDialogUtil.getAlertDialog().dismiss();
            }

            new Handler().postDelayed(() -> {
                NEMeetingSDK.getInstance().getSettingsService().getHistoryMeetingItem((resultCode, resultMsg, resultData) -> {
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
        NEMeetingSDK.getInstance().getMeetingService().removeMeetingStatusListener(listener);
    }
}
