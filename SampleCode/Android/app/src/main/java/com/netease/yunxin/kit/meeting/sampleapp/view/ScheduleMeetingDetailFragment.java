// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.ToastCallback;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.ScheduleMeetingDetailAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.data.ScheduleMeetingDetailItem;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentScheduleDetailBinding;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.ScheduleDetailViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingParams;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAttendeeOffType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAudioControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingLiveAuthLevel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVideoControl;
import com.netease.yunxin.kit.meeting.sdk.NEWatermarkConfig;
import java.util.ArrayList;
import java.util.List;

public class ScheduleMeetingDetailFragment extends BaseFragment<FragmentScheduleDetailBinding> {

  private static final String TAG = ScheduleMeetingDetailFragment.class.getSimpleName();

  private List<ScheduleMeetingDetailItem> dataList = new ArrayList<>();

  private ScheduleMeetingDetailAdapter mAdapter;

  private ScheduleDetailViewModel mViewModel;

  private Bundle arguments;

  private MeetingItem item;

  public static ScheduleMeetingDetailFragment newInstance() {
    return new ScheduleMeetingDetailFragment();
  }

  @Override
  protected FragmentScheduleDetailBinding getViewBinding() {
    return FragmentScheduleDetailBinding.inflate(getLayoutInflater());
  }

  @Override
  protected void initView() {
    mViewModel = ViewModelProviders.of(this).get(ScheduleDetailViewModel.class);
    mAdapter = new ScheduleMeetingDetailAdapter(getActivity(), new ArrayList<>());
    binding.rvScheduleMeeting.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    mAdapter.setHasStableIds(true);
    binding.rvScheduleMeeting.setAdapter(mAdapter);
    mAdapter.setOnItemClickListener(
        (view, position) -> {
          ScheduleMeetingDetailItem item = dataList.get(position);
        });
    mAdapter.setOnChildClickListener(
        (clickAction) -> {
          switch (clickAction) {
            case ScheduleMeetingDetailItem.COPY_MEETING_ID_ACTION:
              copyValue(mAdapter.getData().get(clickAction).getDescription());
              Toast.makeText(getActivity(), "会议Id复制成功", Toast.LENGTH_SHORT).show();
              break;
            case ScheduleMeetingDetailItem.COPY_LIVE_URL_ACTION:
              copyValue(mAdapter.getData().get(clickAction).getDescription());
              Toast.makeText(getActivity(), "直播地址复制成功", Toast.LENGTH_SHORT).show();
              break;
            case ScheduleMeetingDetailItem.COPY_PASSWORD_ACTION:
              copyValue(mAdapter.getData().get(clickAction).getDescription());
              Toast.makeText(getActivity(), "密码复制成功", Toast.LENGTH_SHORT).show();
              break;
          }
        });
    binding.btnJoinScheduleMeeting.setOnClickListener(
        view -> {
          NEJoinMeetingParams params = new NEJoinMeetingParams();
          params.meetingNum = item.getMeetingNum();
          params.password = item.getPassword();
          params.displayName = SdkAuthenticator.getAccount();
          params.watermarkConfig =
              new NEWatermarkConfig(SdkAuthenticator.getAccount(), null, null, null);
          mViewModel.joinMeeting(params, null, new ToastCallback<>(getActivity(), "加入会议"));
        });
    binding.btnCancelScheduleMeeting.setOnClickListener(
        view -> {
          mViewModel.cancelMeeting(
              item.getMeetingId(),
              new ToastCallback<Void>(getActivity(), "scheduleMeeting") {

                @Override
                public void onResult(int resultCode, String resultMessage, Void resultData) {
                  super.onResult(resultCode, resultMessage, resultData);
                  if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                    Navigation.findNavController(getView()).popBackStack();
                  }
                }
              });
        });
    binding.btnEditScheduleMeeting.setOnClickListener(
        view -> {
          Bundle bundle = new Bundle();
          bundle.putSerializable("meetingItem", item);
          bundle.putBoolean("isEditMeeting", true);
          Navigation.findNavController(getView())
              .navigate(
                  R.id.action_scheduleMeetingDetailFragment_to_scheduleMeetingFragment, bundle);
        });
  }

  private void copyValue(String text) {
    //获取剪贴板管理器：
    ClipboardManager cm =
        (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    // 创建普通字符型ClipData
    ClipData mClipData = ClipData.newPlainText("Label", text);
    // 将ClipData内容放到系统剪贴板里。
    cm.setPrimaryClip(mClipData);
  }

  @Override
  protected void initData() {
    //接收传参数据
    arguments = getArguments();
    item = (MeetingItem) arguments.getSerializable("meetingItem");
    if (dataList != null) {
      dataList.clear();
    }
    dataList.add(new ScheduleMeetingDetailItem(item.getSubject(), "", "", 0));
    dataList.add(
        new ScheduleMeetingDetailItem(
            "会议ID",
            String.valueOf(item.getMeetingNum()),
            "复制",
            ScheduleMeetingDetailItem.COPY_MEETING_ID_ACTION));
    dataList.add(new ScheduleMeetingDetailItem("开始时间", "", String.valueOf(item.getStartTime()), 0));
    dataList.add(new ScheduleMeetingDetailItem("结束时间", "", String.valueOf(item.getEndTime()), 0));
    if (!TextUtils.isEmpty(item.getPassword())) {
      dataList.add(
          new ScheduleMeetingDetailItem(
              "会议密码", item.getPassword(), "复制", ScheduleMeetingDetailItem.COPY_PASSWORD_ACTION));
    }

    {
      NEMeetingAudioControl control =
          item.getSetting() != null ? item.getSetting().getCurrentAudioControl() : null;
      if (control != null) {
        {
          ScheduleMeetingDetailItem item = new ScheduleMeetingDetailItem("自动静音", "", "", 0);
          item.setOn(control.getAttendeeOff() != NEMeetingAttendeeOffType.None);
          dataList.add(item);
        }
        {
          ScheduleMeetingDetailItem item = new ScheduleMeetingDetailItem("允许自行解除静音", "", "", 0);
          item.setOn(control.getAttendeeOff() == NEMeetingAttendeeOffType.OffAllowSelfOn);
          dataList.add(item);
        }
      }
    }

    {
      NEMeetingVideoControl control =
          item.getSetting() != null ? item.getSetting().getCurrentVideoControl() : null;
      if (control != null) {
        {
          ScheduleMeetingDetailItem item = new ScheduleMeetingDetailItem("自动关闭视频", "", "", 0);
          item.setOn(control.getAttendeeOff() != NEMeetingAttendeeOffType.None);
          dataList.add(item);
        }
        {
          ScheduleMeetingDetailItem item = new ScheduleMeetingDetailItem("允许自行打开视频", "", "", 0);
          item.setOn(control.getAttendeeOff() == NEMeetingAttendeeOffType.OffAllowSelfOn);
          dataList.add(item);
        }
      }
    }

    if (item.getLive() != null
        && item.getLive().isEnable()
        && !TextUtils.isEmpty(item.getLive().liveUrl())) {
      dataList.add(
          new ScheduleMeetingDetailItem(
              "直播地址",
              item.getLive().liveUrl(),
              "复制",
              ScheduleMeetingDetailItem.COPY_LIVE_URL_ACTION));
    }
    if (item.getLive() != null
        && item.getLive().isEnable()
        && item.getLive().getLiveWebAccessControlLevel() == NEMeetingLiveAuthLevel.appToken) {
      dataList.add(
          new ScheduleMeetingDetailItem(
              "直播模式", "", "仅本企业员工可观看", ScheduleMeetingDetailItem.COPY_LIVE_LEVEL_ACTION));
    }
    if (item.getExtraData() != null) {
      dataList.add(new ScheduleMeetingDetailItem("扩展字段", item.getExtraData(), "", 0));
    }
    NEMeetingItemStatus status = item.getStatus();
    if (status == NEMeetingItemStatus.cancel || status == NEMeetingItemStatus.recycled) {
      binding.btnCancelScheduleMeeting.setVisibility(View.GONE);
      binding.btnJoinScheduleMeeting.setVisibility(View.GONE);
      binding.btnEditScheduleMeeting.setVisibility(View.GONE);
    } else if (status == NEMeetingItemStatus.ended) {
      binding.btnCancelScheduleMeeting.setVisibility(View.GONE);
      binding.btnEditScheduleMeeting.setVisibility(View.GONE);
    } else if (status == NEMeetingItemStatus.init || status == NEMeetingItemStatus.started) {
      binding.btnCancelScheduleMeeting.setVisibility(View.VISIBLE);
      binding.btnJoinScheduleMeeting.setVisibility(View.VISIBLE);
    }
    if (status == NEMeetingItemStatus.started) {
      binding.btnEditScheduleMeeting.setVisibility(View.GONE);
    }
    mAdapter.resetData(dataList);
  }
}
