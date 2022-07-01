// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingParams;


public class StartMeetingViewModel extends AndroidViewModel {

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

    private Context context;

    public StartMeetingViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }


    public void startMeeting(NEStartMeetingParams param, @Nullable NEStartMeetingOptions opts, NECallback<Void> callback) {
        mRepository.startMeeting(context, param, opts, callback);
    }
}
