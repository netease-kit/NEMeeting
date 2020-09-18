package com.netease.meetinglib.demo.viewmodel;


import androidx.lifecycle.ViewModel;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;

public class MainViewModel extends ViewModel {

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();


    public MainViewModel() {
    }


    public void setOnInjectedMenuItemClickListener(NEMeetingOnInjectedMenuItemClickListener listener) {
        mRepository.setOnInjectedMenuItemClickListener(listener);
    }

    public NEMeetingService getMeetingService() {
        return mRepository.getMeetingService();
    }
}
