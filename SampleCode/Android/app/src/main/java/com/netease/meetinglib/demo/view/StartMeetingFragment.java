/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.netease.meetinglib.demo.viewmodel.StartMeetingViewModel;
import com.netease.meetinglib.sdk.NEAccountService;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;


public class StartMeetingFragment extends MeetingCommonFragment {
    private static final String TAG = StartMeetingFragment.class.getSimpleName();
    private String meetingId;
    private String currentMeetingId;
    private String content;
    private StartMeetingViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartMeetingViewModel.class);


        usePersonalMeetingId.setEnabled(true);
        usePersonalMeetingId.setOnCheckedChangeListener((buttonView, isChecked) -> {
            determineMeetingId();
        });
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号(留空或使用个人会议号)", "昵称", "请输入密码"};
    }

    @Override
    protected String getActionLabel() {
        return "创建会议";
    }

    @Override
    protected void performAction(String first, String second, String third) {
        NEStartMeetingParams params = new NEStartMeetingParams();
        params.meetingId = usePersonalMeetingId.isChecked() && !TextUtils.isEmpty(currentMeetingId) ? currentMeetingId : first;
        params.displayName = second;
        NEStartMeetingOptions options = (NEStartMeetingOptions) getMeetingOptions(new NEStartMeetingOptions());

        showDialogProgress("正在创建会议...");
        mViewModel.startMeeting(params, options, new MeetingCallback());
    }


    private void determineMeetingId() {
        if (usePersonalMeetingId.isChecked()) {
            currentMeetingId = meetingId;
            if (!TextUtils.isEmpty(content)) {
                getEditor(0).setText(content);
                return;
            }
            NEAccountService accountService = NEMeetingSDK.getInstance().getAccountService();
            if (accountService == null) {
                onGetPersonalMeetingIdError();
            } else {
                accountService.getAccountInfo((resultCode, resultMsg, resultData) -> {
                    if (!isAdded()) return;
                    if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS && resultData != null) {
                        meetingId = resultData.meetingId;
                        currentMeetingId = meetingId;
                        content = meetingId + (!TextUtils.isEmpty(resultData.shortMeetingId) ? "(短号：" + resultData.shortMeetingId + ")" : "");
                        getEditor(0).setText(content);
                    } else {
                        onGetPersonalMeetingIdError();
                    }
                });
            }
        } else {
            currentMeetingId = null;
            getEditor(0).setText("");
        }
    }

    private void onGetPersonalMeetingIdError() {
        content = null;
        currentMeetingId = null;
        usePersonalMeetingId.setChecked(false);
        Toast.makeText(getActivity(), "获取个人会议号失败", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
