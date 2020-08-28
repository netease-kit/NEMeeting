/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.netease.meetinglib.demo.Constants;
import com.netease.meetinglib.sdk.NEJoinMeetingOptions;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;


public class JoinMeetingFragment extends MeetingBaseFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usePersonalMeetingId.setEnabled(false);
    }

    @Override
    protected String[] getEditorLabel() {
        return new String[]{"会议号","昵称"};
    }

    @Override
    protected String getActionLabel() {
        return isAnonymous() ? "匿名入会" : "加入会议";
    }

    @Override
    protected void performAction(String first, String second) {
        NEJoinMeetingParams params = new NEJoinMeetingParams();
        params.meetingId = first;
        params.displayName = second;
        NEJoinMeetingOptions options = null;
        if (isNotUseDefaultMeetingOptions()) {
            options = new NEJoinMeetingOptions();
            options.noVideo = !isChecked(0);
            options.noAudio = !isChecked(1);
            options.showMeetingTime = true;
        }
        NEMeetingService meetingService = NEMeetingSDK.getInstance().getMeetingService();
        if (meetingService != null) {
            meetingService.joinMeeting(getActivity(), params, options, new MeetingCallback());
            showLoading("正在加入会议...");
        }
    }

    private boolean isAnonymous() {
        return Constants.CONTENT_ID_JOIN_MEETING_ANONYMOUS.equals(getTag());
    }
}
