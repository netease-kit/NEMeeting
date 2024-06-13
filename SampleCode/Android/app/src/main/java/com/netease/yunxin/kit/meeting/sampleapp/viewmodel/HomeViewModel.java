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
import com.netease.yunxin.kit.meeting.sampleapp.utils.DateUtil;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemStatus;
import com.netease.yunxin.kit.meeting.sdk.NEScheduleMeetingStatusListener;
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
    mRepository.getMeetingList(
        status,
        (resultCode, resultMsg, resultData) -> {
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
        NEMeetingItem neMeetingItem = resultData.get(i);
        item.setMeetingNum(neMeetingItem.getMeetingNum());
        item.setPassword(neMeetingItem.getPassword());
        item.setStartTime(neMeetingItem.getStartTime());
        item.setEndTime(neMeetingItem.getEndTime());
        item.setSubject(neMeetingItem.getSubject());
        item.setStatus(neMeetingItem.getStatus());
        item.setMeetingId(neMeetingItem.getMeetingId());
        String startTime = DateUtil.stampToDate(neMeetingItem.getStartTime());
        item.setHourAndMin(startTime.substring(11, 16));
        item.setMonth(startTime.substring(5, 7));
        item.setDay(startTime.substring(8, 10));
        item.setSetting(neMeetingItem.getSetting());
        item.setLive(neMeetingItem.getLive());
        item.setRecurringRule(neMeetingItem.getRecurringRule());
        item.setExtraData(neMeetingItem.getExtraData());
        item.setRoleBinds(neMeetingItem.getRoleBinds());
        item.setNoSip(neMeetingItem.noSip());
        item.setWaitingRoomEnabled(neMeetingItem.isWaitingRoomEnabled());
        item.enableGuestJoin(neMeetingItem.isGuestJoinEnabled());
        items.add(item);
      }
      Collections.sort(items);
      int len = items.size();
      boolean isGroupFirst;
      items.get(0).setGroupFirst(true);
      if (len > 1) {
        for (int i = 1; i < len; i++) {
          isGroupFirst =
              !items.get(i - 1).getDay().equals(items.get(i).getDay())
                  && items.get(i).getMonth().equals(items.get(i).getMonth());
          items.get(i).setGroupFirst(isGroupFirst);
        }
      }
      return items;
    }
    return null;
  }

  public void observeMeetingItems(LifecycleOwner owner, Observer<List<MeetingItem>> observer) {
    listMutableLiveData.observe(owner, observer);
  }

  public void observeChangeMeetingItems(
      LifecycleOwner owner, Observer<List<MeetingItem>> observer) {
    changelistMutableLiveData.observe(owner, observer);
  }

  private NEScheduleMeetingStatusListener listener =
      (changedMeetingItemList, incremental) -> {
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
