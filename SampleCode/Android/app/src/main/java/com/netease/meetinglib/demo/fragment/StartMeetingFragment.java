/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.meetinglib.sdk.NEAccountService;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;


public class StartMeetingFragment extends MeetingBaseFragment {

    private String personalMeetingId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usePersonalMeetingId.setEnabled(true);
        usePersonalMeetingId.setOnCheckedChangeListener((buttonView, isChecked) -> {
            determineMeetingId();
        });

    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号(留空或使用个人会议号)", "昵称", "100", "tittle"};
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
        NEStartMeetingOptions options = null;
        if (isNotUseDefaultMeetingOptions()) {
            options = (NEStartMeetingOptions) getMeetingOptions(new NEStartMeetingOptions());
        }

        showLoading("正在创建会议...");
        getMeetingService().startMeeting(getActivity(), params, options, new MeetingCallback());
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
