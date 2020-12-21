package com.netease.meetinglib.demo.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEJoinMeetingOptions;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.control.NEControlOptions;
import com.netease.meetinglib.sdk.control.NEControlParams;


public class SettingsViewModel extends AndroidViewModel {

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

    private Context context;

    public SettingsViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }


    public void openController(NEControlParams params, NEControlOptions opts, NECallback<Void> callback) {
        mRepository.openController(context, params, opts, callback);
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
