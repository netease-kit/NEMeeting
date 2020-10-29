/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingMenuItem;
import com.netease.meetinglib.sdk.NEMeetingOptions;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.netease.meetinglib.sdk.NEMeetingStatusListener;
import com.netease.meetinglib.sdk.NESettingsService;

import java.util.ArrayList;
import java.util.List;

public abstract class MeetingCommonFragment extends CommonFragment {

    MeetingCommonFragment() {
        super(R.layout.fragment_meeting_base);
    }

    protected CheckBox usePersonalMeetingId;
    private CheckBox[] checkBoxes = new CheckBox[6];
    private CheckBox useDefaultMeetingOptions;
    protected List<NEMeetingMenuItem> injectedMoreMenuItems = new ArrayList<>();

    protected abstract String[] getEditorLabel();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        checkBoxes[0] = view.findViewById(R.id.videoOption);
        checkBoxes[1] = view.findViewById(R.id.audioOption);
        checkBoxes[2] = view.findViewById(R.id.noChatOptions);
        checkBoxes[3] = view.findViewById(R.id.noInviteOptions);
        checkBoxes[4] = view.findViewById(R.id.no_minimize);
        checkBoxes[5] = view.findViewById(R.id.show_meeting_time);

        usePersonalMeetingId = view.findViewById(R.id.usePersonalMeetingId);
        useDefaultMeetingOptions = view.findViewById(R.id.useDefaultOptions);

        String[] labels = getEditorLabel();

        addEditorArray(0, R.id.firstEditor, labels);
        addEditorArray(1, R.id.secondEditor, labels);

        addEditorArray(2, R.id.addItemIdEditor, labels);
        addEditorArray(3, R.id.addTittleEditor, labels);
        addEditorArray(4, R.id.thirdEditor, labels);
        Button addMenuItemButton = getView().findViewById(R.id.addMenuItemButton);
        addMenuItemButton.setOnClickListener(v -> addMenuItem());
        useDefaultMeetingOptions.setChecked(false);
        useDefaultMeetingOptions.setOnCheckedChangeListener((checkbox, checked) -> {
            checkBoxes[0].setEnabled(!checked);
            checkBoxes[0].setChecked(false);
            checkBoxes[1].setEnabled(!checked);
            checkBoxes[1].setChecked(false);
            checkBoxes[5].setEnabled(!checked);
            checkBoxes[5].setChecked(false);
        });
        injectedMoreMenuItems.clear();
        NEMeetingSDK.getInstance().getMeetingService().addMeetingStatusListener(listener);
    }


    private void addMenuItem() {
        String id = getEditorText(2);
        String tittle = getEditorText(3);
        if ("".equals(id) || "".equals(tittle)) {
            Toast.makeText(getActivity(), "参数不允许为空" + id, Toast.LENGTH_SHORT).show();
            return;
        }

        NEMeetingMenuItem meetingMenuItem = new NEMeetingMenuItem();
        assert id != null;
        meetingMenuItem.itemId = Integer.parseInt(id);
        meetingMenuItem.title = tittle;
        injectedMoreMenuItems.add(meetingMenuItem);
        Toast.makeText(getActivity(), "添加自定义菜单成功", Toast.LENGTH_SHORT).show();
    }

    public NEMeetingOptions getMeetingOptions(NEMeetingOptions options) {
        if (isNotUseDefaultMeetingOptions()) {
            options.noVideo = !isChecked(0);
            options.noAudio = !isChecked(1);
            options.showMeetingTime = isChecked(5);
        }else {
            NESettingsService settingsService = NEMeetingSDK.getInstance().getSettingsService();
            options.noVideo = !settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();
            options.noAudio = !settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();
            options.showMeetingTime = settingsService.isShowMyMeetingElapseTimeEnabled();
        }
        options.noChat = isChecked(2);
        options.noInvite = isChecked(3);
        options.noMinimize = isChecked(4);
//        addMeetingInfoItem();
        if (injectedMoreMenuItems != null && injectedMoreMenuItems.size() > 0) {
            options.injectedMoreMenuItems = injectedMoreMenuItems;
        }
        return options;
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
            } else {
                super.onResult(resultCode, resultMsg, resultData);
            }
            if (injectedMoreMenuItems != null) {
                injectedMoreMenuItems.clear();
            }
        }
    }


    private NEMeetingStatusListener listener = event -> {
        if (event.status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
            clear();
            // 增加会议断开连接提示。
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "会议已断开连接", Toast.LENGTH_SHORT).show());
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
