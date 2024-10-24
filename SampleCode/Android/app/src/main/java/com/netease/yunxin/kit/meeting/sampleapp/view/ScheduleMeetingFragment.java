// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.ToastCallback;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.ScheduleMeetingAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.data.ScheduleMeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentScheduleBinding;
import com.netease.yunxin.kit.meeting.sampleapp.utils.CalendarUtil;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.ScheduleViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAttendeeOffType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAudioControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemLive;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemSetting;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingLiveAuthLevel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingRoleType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVideoControl;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleMeetingFragment extends BaseFragment<FragmentScheduleBinding> {

  private static final String TAG = ScheduleMeetingFragment.class.getSimpleName();

  private List<ScheduleMeetingItem> dataList = new ArrayList<>();

  private ScheduleMeetingAdapter mAdapter;

  private ScheduleViewModel mViewModel;

  private long startTime, endTime;
  private Boolean isAttendeeAudioOff, isAllowAttendeeAudioSelfOn;
  private Boolean isAttendeeVideoOff, isAllowAttendeeVideoSelfOn;
  private boolean isUsePwd,
      isLiveOn,
      isLiveLevelOpen,
      isOpenRecord,
      isEnableSip,
      enableWaitingRoom,
      enableGuestJoin;
  private NESettingsService settingsService;
  private boolean isEditMeeting = false;
  private MeetingItem item = null;

  @Override
  protected FragmentScheduleBinding getViewBinding() {
    return FragmentScheduleBinding.inflate(getLayoutInflater());
  }

  @Override
  protected void initView() {
    mViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
    mAdapter =
        new ScheduleMeetingAdapter(
            getActivity(),
            new ArrayList<>(),
            mViewModel.passWord,
            mViewModel.tittle,
            mViewModel.extraData,
            mViewModel.roleBindData);
    binding.rvScheduleMeeting.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    mAdapter.setHasStableIds(true);
    binding.rvScheduleMeeting.setAdapter(mAdapter);
    binding
        .rvScheduleMeeting
        .getRecycledViewPool()
        .setMaxRecycledViews(ScheduleMeetingAdapter.VIEW_TYPE, 0);
    mAdapter.setOnItemClickListener(
            (view, position) -> {
              ScheduleMeetingItem item = dataList.get(position);
              switch (item.getClickAction()) {
                case ScheduleMeetingItem.SET_START_TIME_ACTION:
                  CalendarUtil.showDateTimePickerDialog(
                          getActivity(),
                          startTime,
                          date -> {
                            startTime = date;
                            mAdapter
                                    .getData()
                                    .get(ScheduleMeetingItem.SET_START_TIME_ACTION)
                                    .setTimeTip(formatTime(date));
                            mAdapter.updateData(
                                    ScheduleMeetingItem.SET_START_TIME_ACTION,
                                    mAdapter.getData().get(ScheduleMeetingItem.SET_START_TIME_ACTION));
                          });
                  break;
                case ScheduleMeetingItem.SET_END_TIME_ACTION:
                  CalendarUtil.showDateTimePickerDialog(
                          getActivity(),
                          endTime,
                          date -> {
                            //TODO 必须大于当前开始时间
                            endTime = date;
                            mAdapter
                                    .getData()
                                    .get(ScheduleMeetingItem.SET_END_TIME_ACTION)
                                    .setTimeTip(formatTime(date));
                            mAdapter.updateData(
                                    ScheduleMeetingItem.SET_END_TIME_ACTION,
                                    mAdapter.getData().get(ScheduleMeetingItem.SET_END_TIME_ACTION));
                          });
                  break;
              }
            });
    mAdapter.setOnCheckedChangeListener(
        (compoundButton, enable, clickAction) -> {
          if (compoundButton != null && compoundButton.getId() == R.id.sb_meeting_switch) {
            switch (clickAction) {
              case ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION:
                isUsePwd = enable;
                break;
              case ScheduleMeetingItem.SET_AUDIO_MUTE_ACTION:
                isAttendeeAudioOff = enable;
                break;
              case ScheduleMeetingItem.SET_VIDEO_MUTE_ACTION:
                isAttendeeVideoOff = enable;
                break;
              case ScheduleMeetingItem.SET_ALLOW_AUDIO_ON_ACTION:
                isAllowAttendeeAudioSelfOn = enable;
                break;
              case ScheduleMeetingItem.SET_ALLOW_VIDEO_ON_ACTION:
                isAllowAttendeeVideoSelfOn = enable;
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_LIVE_ACTION:
                isLiveOn = enable;
                if (settingsService.isMeetingLiveSupported() && isLiveOn) {
                  if (binding.rvScheduleMeeting.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                      && (!binding.rvScheduleMeeting.isComputingLayout())) {
                    mAdapter.addNewData(
                        mAdapter.getItemCount(),
                        new ScheduleMeetingItem(
                            "仅本企业员工可观看",
                            false,
                            ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION,
                            ""));
                  }
                } else {
                  if (binding.rvScheduleMeeting.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                      && (!binding.rvScheduleMeeting.isComputingLayout())) {
                    mAdapter.deleteItem(mAdapter.getItemCount() - 1);
                  }
                }
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION:
                isLiveLevelOpen = enable;
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION:
                isOpenRecord = enable;
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_NO_SIP_ACTION:
                isEnableSip = enable;
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_WAITING_ROOM:
                enableWaitingRoom = enable;
                break;
              case ScheduleMeetingItem.ENABLE_MEETING_GUEST_JOIN:
                enableGuestJoin = enable;
                break;
            }
          }
        });
    mViewModel.observeItemMutableLiveData(
        getActivity(),
        new Observer<NEMeetingItem>() {

          @Override
          public void onChanged(NEMeetingItem neMeetingItem) {
            if (neMeetingItem != null) {
              neMeetingItem
                  .setSubject(mViewModel.tittle.getValue())
                  .setStartTime(startTime)
                  .setEndTime(endTime);
              if (isUsePwd) {
                neMeetingItem.setPassword(mViewModel.passWord.getValue());
              }
              neMeetingItem.setExtraData(mViewModel.extraData.getValue());
              String roleBindsStr = mViewModel.roleBindData.getValue();
              if (!TextUtils.isEmpty(roleBindsStr)) {
                try {
                  JSONObject roleBindJson = new JSONObject(roleBindsStr);
                  Map<String, NEMeetingRoleType> roleBinds = new HashMap<>();
                  Iterator<String> iterator = roleBindJson.keys();
                  while (iterator.hasNext()) {
                    String key = iterator.next();
                    NEMeetingRoleType roleType =
                        NEMeetingRoleType.values()[roleBindJson.optInt(key)];
                    roleBinds.put(key, roleType);
                  }
                  neMeetingItem.setRoleBinds(roleBinds);
                } catch (JSONException e) {
                  Toast.makeText(getContext(), "绑定角色数据结构出错了", Toast.LENGTH_SHORT).show();
                  return;
                }
              }
              NEMeetingItemSetting setting = new NEMeetingItemSetting();
              List<NEMeetingControl> controls = null;
              if (isAttendeeAudioOff == Boolean.TRUE) {
                controls = new ArrayList<>();
                NEMeetingAudioControl control = new NEMeetingAudioControl();
                controls.add(control);
                control.setAttendeeOff(
                    isAllowAttendeeAudioSelfOn
                        ? NEMeetingAttendeeOffType.OffAllowSelfOn
                        : NEMeetingAttendeeOffType.OffNotAllowSelfOn);
              }
              if (isAttendeeVideoOff == Boolean.TRUE) {
                if (controls == null) {
                  controls = new ArrayList<>();
                }
                NEMeetingVideoControl control = new NEMeetingVideoControl();
                controls.add(control);
                control.setAttendeeOff(
                    isAllowAttendeeVideoSelfOn
                        ? NEMeetingAttendeeOffType.OffAllowSelfOn
                        : NEMeetingAttendeeOffType.OffNotAllowSelfOn);
              }
              if (controls != null) {
                setting.controls = controls;
              }
              setting.cloudRecordOn = isOpenRecord;
              neMeetingItem.setSetting(setting);
              NEMeetingItemLive live =
                  NEMeetingKit.getInstance().getPreMeetingService().createMeetingItemLive();
              live.setEnable(isLiveOn);
              live.setLiveWebAccessControlLevel(
                  isLiveLevelOpen ? NEMeetingLiveAuthLevel.appToken : NEMeetingLiveAuthLevel.token);
              neMeetingItem.setLive(live);
              neMeetingItem.setNoSip(!isEnableSip);
              neMeetingItem.setWaitingRoomEnabled(enableWaitingRoom);
              neMeetingItem.setEnableGuestJoin(enableGuestJoin);
              if (isEditMeeting) {
                mViewModel.editMeeting(
                    neMeetingItem,
                    new ToastCallback<NEMeetingItem>(getActivity(), "editMeeting") {
                      @Override
                      public void onResult(
                          int resultCode, String resultMessage, NEMeetingItem resultData) {
                        super.onResult(resultCode, resultMessage, resultData);
                        if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                          Navigation.findNavController(getView())
                              .popBackStack(R.id.homeFragment, false);
                        }
                      }
                    });
              } else {
                mViewModel.scheduleMeeting(
                    neMeetingItem,
                    new ToastCallback<NEMeetingItem>(getActivity(), "scheduleMeeting") {

                      @Override
                      public void onResult(
                          int resultCode, String resultMessage, NEMeetingItem resultData) {
                        super.onResult(resultCode, resultMessage, resultData);
                        if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                          Navigation.findNavController(getView()).popBackStack();
                        }
                      }
                    });
              }
            }
          }
        });
    binding.btnStartScheduleMeeting.setOnClickListener(
        view -> {
          if (!isEditMeeting) {
            mViewModel.createScheduleMeetingItem();
          } else {
            mViewModel.mapToNEMeetingItem(item);
          }
        });
  }

  private String formatTime(long time) {
    if (time == 0) {
      return "";
    }
    SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
    dateFormat.applyPattern("yyyy-MM-dd HH:mm");
    return dateFormat.format(new Date(time));
  }

  @Override
  protected void initData() {
    if (dataList != null) {
      dataList.clear();
    }
    handleArguments();
    dataList.add(
        new ScheduleMeetingItem(
            "会议主题",
            ScheduleMeetingItem.EDIT_TEXT_TITLE_ACTION,
            item != null ? item.getSubject() : ""));
    dataList.add(
        new ScheduleMeetingItem(
            "开始时间",
            "请选择开始入会时间",
            ScheduleMeetingItem.SET_START_TIME_ACTION,
            item != null ? formatTime(item.getStartTime()) : ""));
    dataList.add(
        new ScheduleMeetingItem(
            "结束时间",
            "请选择会议结束时间",
            ScheduleMeetingItem.SET_END_TIME_ACTION,
            item != null ? formatTime(item.getEndTime()) : ""));
    dataList.add(
        new ScheduleMeetingItem(
            "会议密码",
            (item != null && isUsePwd),
            ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION,
            item != null ? item.getPassword() : ""));
    handleAudioSetting();
    handleVideoSetting();
    handleLiveSetting();
    dataList.add(
        new ScheduleMeetingItem(
            "开启录制",
            (item != null && item.getSetting().cloudRecordOn),
            ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION,
            ""));
    dataList.add(
        new ScheduleMeetingItem(
            "开启SIP",
            (item != null && !item.isNoSip()),
            ScheduleMeetingItem.ENABLE_MEETING_NO_SIP_ACTION,
            ""));
    dataList.add(
        new ScheduleMeetingItem(
            "开启等候室",
            (item != null && item.isWaitingRoomEnabled()),
            ScheduleMeetingItem.ENABLE_MEETING_WAITING_ROOM,
            ""));
    dataList.add(
        new ScheduleMeetingItem(
            "允许访客入会",
            (item != null && item.isGuestJoinEnabled()),
            ScheduleMeetingItem.ENABLE_MEETING_GUEST_JOIN,
            ""));
    dataList.add(
        new ScheduleMeetingItem(
            "扩展字段",
            ScheduleMeetingItem.SET_EXTRA_DATA_ACTION,
            item != null ? item.getExtraData() : ""));
    handleRoleBind();
    handleLiveLevel();
    mAdapter.resetData(dataList);
  }

  /// 处理设置直播等级
  private void handleLiveLevel() {
    if (item != null && item.getLive().isEnable()) {
      dataList.add(
          new ScheduleMeetingItem(
              "仅本企业员工可观看",
              (item != null
                  && item.getLive().getLiveWebAccessControlLevel()
                      == NEMeetingLiveAuthLevel.appToken),
              ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION,
              ""));
    }
  }

  /// 处理直播设置
  private void handleLiveSetting() {
    settingsService = NEMeetingKit.getInstance().getSettingsService();
    if (settingsService.isMeetingLiveSupported()) {
      dataList.add(
          new ScheduleMeetingItem(
              "开启直播",
              (item != null && item.getLive().isEnable()),
              ScheduleMeetingItem.ENABLE_MEETING_LIVE_ACTION,
              ""));
    }
  }

  /// 处理角色绑定
  private void handleRoleBind() {
    Map<String, NEMeetingRoleType> roleBinds = null;
    JSONObject roleBindsJson = null;
    if (item != null) {
      roleBinds = item.getRoleBinds();
      try {
        if (roleBinds != null) {
          roleBindsJson = new JSONObject();
          Iterator<String> iterator = roleBinds.keySet().iterator();
          while (iterator.hasNext()) {
            String key = iterator.next();
            NEMeetingRoleType roleType = roleBinds.get(key);
            roleBindsJson.put(key, roleType.getRoleType());
          }
        }
      } catch (JSONException e) {

      } finally {
        dataList.add(
            new ScheduleMeetingItem(
                "角色绑定",
                ScheduleMeetingItem.SET_ROLE_BIND,
                roleBindsJson != null ? roleBindsJson.toString() : ""));
      }
    } else {
      dataList.add(new ScheduleMeetingItem("角色绑定", ScheduleMeetingItem.SET_ROLE_BIND, ""));
    }
  }

  /// 处理视频设置
  private void handleVideoSetting() {
    boolean isVideoSwitchOn = false;
    boolean videoOffAllowSelfOn = true;
    if (item != null && item.getSetting().getCurrentVideoControl() != null) {
      isVideoSwitchOn =
          item.getSetting().getCurrentVideoControl().getAttendeeOff()
              != NEMeetingAttendeeOffType.None;
      videoOffAllowSelfOn =
          item.getSetting().getCurrentVideoControl().getAttendeeOff()
              == NEMeetingAttendeeOffType.OffAllowSelfOn;
    }
    dataList.add(
        new ScheduleMeetingItem(
            "自动关闭视频",
            "参会者加入会议时自动关闭视频",
            isVideoSwitchOn,
            ScheduleMeetingItem.SET_VIDEO_MUTE_ACTION));
    dataList.add(
        new ScheduleMeetingItem(
            "允许自行打开视频",
            "参会者被自动关闭视频后可自行打开",
            videoOffAllowSelfOn,
            ScheduleMeetingItem.SET_ALLOW_VIDEO_ON_ACTION));
  }

  /// 处理音频设置
  private void handleAudioSetting() {
    boolean isAudioSwitchOn = false;
    boolean audioOffAllowSelfOn = true;
    if (item != null && item.getSetting().getCurrentAudioControl() != null) {
      isAudioSwitchOn =
          item.getSetting().getCurrentAudioControl().getAttendeeOff()
              != NEMeetingAttendeeOffType.None;
      audioOffAllowSelfOn =
          item.getSetting().getCurrentAudioControl().getAttendeeOff()
              == NEMeetingAttendeeOffType.OffAllowSelfOn;
    }
    dataList.add(
        new ScheduleMeetingItem(
            "自动静音", "参会者加入会议时自动静音", isAudioSwitchOn, ScheduleMeetingItem.SET_AUDIO_MUTE_ACTION));
    dataList.add(
        new ScheduleMeetingItem(
            "允许自行解除静音",
            "参会者被自动静音后可自行打开",
            audioOffAllowSelfOn,
            ScheduleMeetingItem.SET_ALLOW_AUDIO_ON_ACTION));
  }

  /// 处理预约会议数据，编辑会议使用
  private void handleArguments() {
    Bundle arguments = getArguments();
    if (arguments != null && arguments.containsKey("meetingItem")) {
      item = (MeetingItem) arguments.getSerializable("meetingItem");
    }
    if (item != null) {
      binding.btnStartScheduleMeeting.setText("编辑会议");
      startTime = item.getStartTime();
      endTime = item.getEndTime();
      isUsePwd = !TextUtils.isEmpty(item.getPassword());
      mViewModel.passWord.setValue(item.getPassword());
      mViewModel.tittle.setValue(item.getSubject());
      mViewModel.extraData.setValue(item.getExtraData());
      if (item.getSetting().getCurrentAudioControl() != null) {
        isAttendeeAudioOff =
            item.getSetting().getCurrentAudioControl().getAttendeeOff()
                != NEMeetingAttendeeOffType.None;
        isAllowAttendeeAudioSelfOn =
            item.getSetting().getCurrentAudioControl().getAttendeeOff()
                == NEMeetingAttendeeOffType.OffAllowSelfOn;
      }
      if (item.getSetting().getCurrentVideoControl() != null) {
        isAttendeeVideoOff =
            item.getSetting().getCurrentVideoControl().getAttendeeOff()
                != NEMeetingAttendeeOffType.None;
        isAllowAttendeeVideoSelfOn =
            item.getSetting().getCurrentVideoControl().getAttendeeOff()
                == NEMeetingAttendeeOffType.OffAllowSelfOn;
      }

      isEditMeeting = true;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    CalendarUtil.closeOptionsMenu();
  }
}
