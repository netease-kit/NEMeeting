/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.sdk.NEAccountService;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;

public class StartMeetingFragment extends MeetingBaseFragment {

    private CheckBox usePersonalMeetingId;
    private String personalMeetingId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usePersonalMeetingId = getView().findViewById(R.id.usePersonalMeetingId);
        usePersonalMeetingId.setVisibility(View.VISIBLE);
        usePersonalMeetingId.setOnCheckedChangeListener((buttonView, isChecked) -> {
            determineMeetingId();
        });
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号(留空或使用个人会议号)","昵称"};
    }

    @Override
    protected String getActionLabel() {
        return "创建会议";
    }

    @Override
    protected void performAction(String first, String second) {
        NEStartMeetingParams params = new NEStartMeetingParams();
        params.meetingId = first;
        params.displayName = second;
        NEStartMeetingOptions options = new NEStartMeetingOptions();
        options.noVideo = !isChecked(0);
        options.noAudio = !isChecked(1);
        NEMeetingService meetingService = NEMeetingSDK.getInstance().getMeetingService();
        if (meetingService != null) {
            meetingService.startMeeting(getActivity(), params, options, new MeetingCallback());
            showLoading("正在创建会议...");
        }
    }

    private void determineMeetingId() {
        if (usePersonalMeetingId.isChecked()) {
            if (!TextUtils.isEmpty(personalMeetingId)) {
                getEditor(0).setText(personalMeetingId);
                return;
            }
            NEAccountService accountService = NEMeetingSDK.getInstance().getAccountService();
            if (accountService == null) {
                onGetPersonalMeetingIdError();
            } else {
                accountService.getPersonalMeetingId((resultCode, resultMsg, resultData) -> {
                    if (!isAdded()) return;
                    if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS && !TextUtils.isEmpty(resultData)) {
                        personalMeetingId = resultData;
                        getEditor(0).setText(resultData);
                    } else {
                        onGetPersonalMeetingIdError();
                    }
                });
            }
        } else {
            getEditor(0).setText("");
        }
    }

    private void onGetPersonalMeetingIdError() {
        personalMeetingId = null;
        usePersonalMeetingId.setChecked(false);
        Toast.makeText(getActivity(), "获取个人会议号失败", Toast.LENGTH_SHORT).show();
    }
}
