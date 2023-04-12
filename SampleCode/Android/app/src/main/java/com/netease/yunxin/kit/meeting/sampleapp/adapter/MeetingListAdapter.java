// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
package com.netease.yunxin.kit.meeting.sampleapp.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.ItemMeetingListBinding;
import com.netease.yunxin.kit.meeting.sampleapp.utils.DateUtil;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemStatus;
import java.util.List;

public class MeetingListAdapter extends BaseAdapter<MeetingItem, ItemMeetingListBinding> {

  public MeetingListAdapter(List<MeetingItem> data) {
    super(data);
  }

  @Override
  public ItemMeetingListBinding getViewBinding(ViewGroup parent, int viewType) {
    return ItemMeetingListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void convert(MeetingItem data, int position, VH<ItemMeetingListBinding> vh) {
    final ItemMeetingListBinding binding = vh.viewBinding;
    //yyyy-MM-dd HH:mm:ss
    String startTime = DateUtil.stampToDate(data.getStartTime());
    String hourAndMin = startTime.substring(11, 16);
    String month = startTime.substring(5, 7);
    String day = startTime.substring(8, 10);
    binding.tvMeetingListTittle.setText(data.getSubject());
    binding.tvMeetingId.setText(" |  会议号：" + data.getMeetingNum());
    binding.tvMeetingTimeMonth.setText(month + obtainDayTip(day));
    binding.tvMeetingTimeHh.setText(hourAndMin);
    binding.tvMeetingListDay.setText(day);
    binding.imgMeetingStatus.setText(meetingStatusToTip(data.getStatus()));
    binding.gpMeetingGroup.setVisibility(data.isGroupFirst() ? View.VISIBLE : View.GONE);
  }

  private String obtainDayTip(String day) {
    String dayTip = "";
    try {
      int currentDay = Integer.parseInt(DateUtil.getDay());
      if (currentDay == Integer.valueOf(day)) {
        dayTip = "  今天";
      } else if (currentDay + 1 == Integer.valueOf(day)) {
        dayTip = "  明天";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dayTip;
  }

  private String meetingStatusToTip(NEMeetingItemStatus status) {
    String tip;

    switch (status) {
      case init:
        tip = "未开始";
        break;
      case started:
        tip = "进行中";
        break;
      case ended:
        tip = "已结束";
        break;
      case cancel:
        tip = "已取消";
        break;
      case recycled:
        tip = "已回收";
        break;
      default:
        tip = "无效状态";
    }

    return tip;
  }
}
