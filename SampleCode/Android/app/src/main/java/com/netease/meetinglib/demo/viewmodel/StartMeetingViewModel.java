package com.netease.meetinglib.demo.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;


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
