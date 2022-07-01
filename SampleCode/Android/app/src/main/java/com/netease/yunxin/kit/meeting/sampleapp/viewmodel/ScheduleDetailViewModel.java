// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingParams;


public class ScheduleDetailViewModel extends AndroidViewModel {

    private Context context;
    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

    public ScheduleDetailViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public void cancelMeeting(long meetingUniqueId, NECallback<Void> callback) {
        mRepository.cancelMeeting(meetingUniqueId, callback);
    }

    public void joinMeeting(@NonNull NEJoinMeetingParams param, @Nullable NEJoinMeetingOptions opts, NECallback<Void> callback) {
        mRepository.joinMeeting(context, param, opts, callback);
    }

}
