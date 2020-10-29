package com.netease.meetinglib.demo.viewmodel;


import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.netease.meetinglib.demo.data.MeetingDataRepository;
import com.netease.meetinglib.demo.data.MeetingItem;
import com.netease.meetinglib.demo.utils.DateUtil;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEMeetingItem;
import com.netease.meetinglib.sdk.NEMeetingItemStatus;
import com.netease.meetinglib.sdk.NEScheduleMeetingStatusListener;
import com.netease.meetinglib.sdk.control.NEControlOptions;
import com.netease.meetinglib.sdk.control.NEControlParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();
    private MutableLiveData<List<MeetingItem>> listMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MeetingItem>> changelistMutableLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        registerScheduleMeetingStatusListener(listener);
    }

    public void registerScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
        this.listener = listener;
        mRepository.registerScheduleMeetingStatusListener(listener);
    }

    public void getMeetingList() {
        List<NEMeetingItemStatus> status = new ArrayList<>();
        status.add(NEMeetingItemStatus.init);
        status.add(NEMeetingItemStatus.started);
        status.add(NEMeetingItemStatus.ended);
        mRepository.getMeetingList(status, (resultCode, resultMsg, resultData) -> {
            listMutableLiveData.setValue(sort(resultData));
        });
    }
    private List<MeetingItem> sort(List<NEMeetingItem> resultData) {
        if (resultData == null) {
            return null;
        }
        if (resultData.size() >= 1) {
            List<MeetingItem> items = new ArrayList<>();
            for (int i = 0; i < resultData.size(); i++) {
                MeetingItem item = new MeetingItem();
                item.setMeetingId(resultData.get(i).getMeetingId());
                item.setPassword(resultData.get(i).getPassword());
                item.setStartTime(resultData.get(i).getStartTime());
                item.setEndTime(resultData.get(i).getEndTime());
                item.setSubject(resultData.get(i).getSubject());
                item.setStatus(resultData.get(i).getStatus());
                item.setUpdateTime(resultData.get(i).getUpdateTime());
                item.setCreateTime(resultData.get(i).getCreateTime());
                item.setMeetingUniqueId(resultData.get(i).getMeetingUniqueId());
                String startTime = DateUtil.stampToDate(resultData.get(i).getStartTime());
                item.setHourAndMin(startTime.substring(11, 16));
                item.setMonth(startTime.substring(5, 7));
                item.setDay(startTime.substring(8, 10));
                MeetingItem.MeetingItemSetting setting = new MeetingItem.MeetingItemSetting();
                setting.setAttendeeAudioOff(resultData.get(i).getSetting().isAttendeeAudioOff);
                item.setSetting(setting);
                items.add(item);
            }
            Collections.sort(items);
            items.get(0).setGroupFirst(true);
            for (int i = 1; i < items.size(); i++) {
                boolean isGroupFirst = !items.get(0).getDay().equals(items.get(i).getDay()) && items.get(0).getMonth().equals(items.get(i).getMonth());
                items.get(i).setGroupFirst(isGroupFirst);
            }

            return items;
        }
        return null;
    }

    public void observeMeetingItems(LifecycleOwner owner, Observer<List<MeetingItem>> observer) {
        listMutableLiveData.observe(owner, observer);
    }

    public void observeChangeMeetingItems(LifecycleOwner owner, Observer<List<MeetingItem>> observer) {
        changelistMutableLiveData.observe(owner, observer);
    }

    private NEScheduleMeetingStatusListener listener = (changedMeetingItemList, incremental) -> {
        if (changedMeetingItemList == null || changedMeetingItemList.size() <= 0) {
            return;
        }
        getMeetingList();
//        changelistMutableLiveData.setValue(sort(changedMeetingItemList));
    };

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.unRegisterScheduleMeetingStatusListener(listener);
    }
}
