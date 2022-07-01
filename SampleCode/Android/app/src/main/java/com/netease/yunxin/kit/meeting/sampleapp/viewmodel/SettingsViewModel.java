// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sdk.NECallback;


public class SettingsViewModel extends AndroidViewModel {

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

    private Context context;

    public SettingsViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public void openBeautyUI( NECallback<Void> callback) {
        mRepository.openBeautyUI(context, callback);
    }

    public boolean isBeautyFaceEnabled() {
        return mRepository.isBeautyFaceEnabled();
    }

    public void getBeautyFaceValue(NECallback<Integer> callback) {
         mRepository.getBeautyFaceValue(callback);
    }

    public void setBeautyFaceValue(int beautyFaceValue) {
         mRepository.setBeautyFaceValue(beautyFaceValue);
    }
}
