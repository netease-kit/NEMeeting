// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItem;

public class ScheduleViewModel extends ViewModel {
  public MutableLiveData<String> passWord = new MutableLiveData<>();
  public MutableLiveData<String> tittle = new MutableLiveData<>();
  public MutableLiveData<String> extraData = new MutableLiveData<>();
  public MutableLiveData<String> roleBindData = new MutableLiveData<>();
  public MutableLiveData<NEMeetingItem> itemMutableLiveData = new MutableLiveData<>();

  private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

  public ScheduleViewModel() {
    super();
  }

  public void createScheduleMeetingItem() {
    itemMutableLiveData.setValue(mRepository.createScheduleMeetingItem());
  }

  public void mapToNEMeetingItem(MeetingItem item) {
    NEMeetingItem meetingItem = mRepository.createScheduleMeetingItem();
    meetingItem.setEndTime(item.getEndTime());
    meetingItem.setExtraData(item.getExtraData());
    meetingItem.setLive(item.getLive());
    meetingItem.setRecurringRule(item.getRecurringRule());
    meetingItem.setPassword(item.getPassword());
    meetingItem.setSetting(item.getSetting());
    meetingItem.setSubject(item.getSubject());
    meetingItem.setRoleBinds(item.getRoleBinds());
    meetingItem.setStartTime(item.getStartTime());
    meetingItem.setMeetingNum(item.getMeetingNum());
    meetingItem.setNoSip(item.isNoSip());
    meetingItem.setMeetingId(item.getMeetingId());
    meetingItem.setEnableGuestJoin(item.isGuestJoinEnabled());
    itemMutableLiveData.setValue(meetingItem);
  }

  public void scheduleMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
    mRepository.scheduleMeeting(item, callback);
  }

  public void editMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
    mRepository.editMeeting(item, callback);
  }

  public void observeItemMutableLiveData(LifecycleOwner owner, Observer<NEMeetingItem> observer) {
    itemMutableLiveData.observe(owner, observer);
  }
}
