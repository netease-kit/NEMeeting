/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.viewmodel.JoinMeetingViewModel;
import com.netease.meetinglib.sdk.NEJoinMeetingOptions;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;


public class JoinMeetingFragment extends MeetingCommonFragment {
    private static final String TAG = JoinMeetingFragment.class.getSimpleName();
    private JoinMeetingViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usePersonalMeetingId.setEnabled(false);
        mViewModel = ViewModelProviders.of(this).get(JoinMeetingViewModel.class);
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号", "昵称", "100", "tittle","请输入密码"};
    }

    @Override
    protected String getActionLabel() {
        return isAnonymous() ? "匿名入会" : "加入会议";
    }

    @Override
    protected void performAction(String first, String second,String third) {
        NEJoinMeetingParams params = new NEJoinMeetingParams();
        params.meetingId = first;
        params.displayName = second;
        params.password = third;
        NEJoinMeetingOptions options = (NEJoinMeetingOptions) getMeetingOptions(new NEJoinMeetingOptions());
        showDialogProgress("正在加入会议...");
        mViewModel.joinMeeting(params, options, new MeetingCallback());
    }

    private boolean isAnonymous() {
        return !SdkAuthenticator.getInstance().isAuthorized();
    }
}
