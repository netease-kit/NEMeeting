package com.netease.meetinglib.demo.viewmodel;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEJoinMeetingOptions;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.NEMeetingItem;
import com.netease.meetinglib.sdk.NEMeetingSDK;


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
