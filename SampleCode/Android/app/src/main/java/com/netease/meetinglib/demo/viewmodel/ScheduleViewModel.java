package com.netease.meetinglib.demo.viewmodel;


import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEMeetingItem;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEScheduleMeetingStatusListener;


public class ScheduleViewModel extends ViewModel {
    public MutableLiveData<String> passWord = new MutableLiveData<>();
    public MutableLiveData<String> tittle = new MutableLiveData<>();
    public MutableLiveData<NEMeetingItem> itemMutableLiveData = new MutableLiveData<>();

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

    public ScheduleViewModel() {
        super();
    }

    public void createScheduleMeetingItem() {
        itemMutableLiveData.setValue(mRepository.createScheduleMeetingItem());
    }

    public void scheduleMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
        mRepository.scheduleMeeting(item, callback);
    }


    public void observeItemMutableLiveData(LifecycleOwner owner, Observer<NEMeetingItem> observer) {
        itemMutableLiveData.observe(owner, observer);
    }
}
