/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.manu.mdatepicker.MDatePickerDialog;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.ToastCallback;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.ScheduleMeetingAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.data.ScheduleMeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.utils.CalendarUtil;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.ScheduleViewModel;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentScheduleBinding;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAttendeeOffType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingAudioControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingControl;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemLive;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemSetting;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingLiveAuthLevel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVideoControl;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONObject;

public class ScheduleMeetingFragment extends BaseFragment<FragmentScheduleBinding> {

    private static final String TAG = ScheduleMeetingFragment.class.getSimpleName();

    private List<ScheduleMeetingItem> dataList = new ArrayList<>();

    private ScheduleMeetingAdapter mAdapter;

    private ScheduleViewModel mViewModel;

    private long startTime, endTime;
    private JSONObject jsonScene;

    private Boolean isAttendeeAudioOff, isAllowAttendeeAudioSelfOn;
    private Boolean isAttendeeVideoOff, isAllowAttendeeVideoSelfOn;

    private boolean isUsePwd, isLiveOn,isLiveLevelOpen,isOpenRecord;
    private NESettingsService settingsService;

    public static ScheduleMeetingFragment newInstance() {
        return new ScheduleMeetingFragment();
    }

    @Override
    protected FragmentScheduleBinding getViewBinding() {
        return FragmentScheduleBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        mAdapter = new ScheduleMeetingAdapter(getActivity(), new ArrayList<>(), mViewModel.passWord, mViewModel.tittle, mViewModel.extraData);
        binding.rvScheduleMeeting.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter.setHasStableIds(true);
        binding.rvScheduleMeeting.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, position) -> {
            ScheduleMeetingItem item = dataList.get(position);
            switch (item.getClickAction()) {
                case ScheduleMeetingItem.SET_START_TIME_ACTION:
                    CalendarUtil.showDatePickerDialog(getActivity(), new MDatePickerDialog.OnDateResultListener() {

                        @Override
                        public void onDateResult(long date) {
                            startTime = date;
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(date);
                            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                            dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                            mAdapter.getData().get(ScheduleMeetingItem.SET_START_TIME_ACTION).setTimeTip(
                                    dateFormat.format(new Date(date)));
                            mAdapter.updateData(ScheduleMeetingItem.SET_START_TIME_ACTION,
                                                mAdapter.getData().get(ScheduleMeetingItem.SET_START_TIME_ACTION));
                        }
                    });
                    break;
                case ScheduleMeetingItem.SET_END_TIME_ACTION:
                    CalendarUtil.showDatePickerDialog(getActivity(), new MDatePickerDialog.OnDateResultListener() {

                        @Override
                        public void onDateResult(long date) {
                            //TODO 必须大于当前开始时间
                            endTime = date;
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(date);
                            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                            dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                            mAdapter.getData().get(ScheduleMeetingItem.SET_END_TIME_ACTION).setTimeTip(
                                    dateFormat.format(new Date(date)));
                            mAdapter.updateData(ScheduleMeetingItem.SET_END_TIME_ACTION,
                                                mAdapter.getData().get(ScheduleMeetingItem.SET_END_TIME_ACTION));
                        }
                    });
                    break;
            }

        });
        mAdapter.setOnCheckedChangeListener((compoundButton, enable, clickAction) -> {
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
                        if(settingsService.isMeetingLiveEnabled()&& isLiveOn){
                            mAdapter.addNewData(mAdapter.getItemCount(),new ScheduleMeetingItem("仅本企业员工可观看", false,
                                                                                                ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION));
                        }else {
                            mAdapter.deleteItem(mAdapter.getItemCount() -1);
                        }
                        break;
                    case ScheduleMeetingItem.ENABLE_MEETING_LIVE_LEVEL_ACTION:
                        isLiveLevelOpen = enable;
                        break;
                    case ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION:
                        isOpenRecord = enable;
                        break;
                }
            }
        });
        mViewModel.observeItemMutableLiveData(getActivity(), new Observer<NEMeetingItem>() {

            @Override
            public void onChanged(NEMeetingItem neMeetingItem) {
                if (neMeetingItem != null) {
                    neMeetingItem.setSubject(mViewModel.tittle.getValue()).setStartTime(startTime).setEndTime(endTime);
                    if (isUsePwd) {
                        neMeetingItem.setPassword(mViewModel.passWord.getValue());
                    }
                    neMeetingItem.setExtraData(mViewModel.extraData.getValue());
                    NEMeetingItemSetting setting = new NEMeetingItemSetting();
                    List<NEMeetingControl> controls = null;
                    if (isAttendeeAudioOff == Boolean.TRUE) {
                        controls = new ArrayList<>();
                        NEMeetingAudioControl control = new NEMeetingAudioControl();
                        controls.add(control);
                        control.setAttendeeOff(isAllowAttendeeAudioSelfOn ?
                                NEMeetingAttendeeOffType.OffAllowSelfOn :
                                NEMeetingAttendeeOffType.OffNotAllowSelfOn
                        );
                    }
                    if (isAttendeeVideoOff == Boolean.TRUE) {
                        if (controls == null) {
                            controls = new ArrayList<>();
                        }
                        NEMeetingVideoControl control = new NEMeetingVideoControl();
                        controls.add(control);
                        control.setAttendeeOff(isAllowAttendeeVideoSelfOn ?
                                NEMeetingAttendeeOffType.OffAllowSelfOn :
                                NEMeetingAttendeeOffType.OffNotAllowSelfOn
                        );
                    }
                    if (controls != null) {
                        setting.controls = controls;
                    }
                    setting.cloudRecordOn = isOpenRecord;
                    neMeetingItem.setSetting(setting);
                    NEMeetingItemLive live = NEMeetingKit.getInstance().getPreMeetingService().createMeetingItemLive();
                    live.setEnable(isLiveOn);
                    live.setLiveWebAccessControlLevel(isLiveLevelOpen? NEMeetingLiveAuthLevel.appToken:NEMeetingLiveAuthLevel.token);
                    neMeetingItem.setLive(live);
                    mViewModel.scheduleMeeting(neMeetingItem,
                                               new ToastCallback<NEMeetingItem>(getActivity(), "scheduleMeeting") {

                                                   @Override
                                                   public void onResult(int resultCode, String resultMsg,
                                                                        NEMeetingItem resultData) {
                                                       super.onResult(resultCode, resultMsg, resultData);
                                                       if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                                                           Navigation.findNavController(getView()).popBackStack();
                                                       }
                                                   }
                                               });
                }
            }
        });
        binding.btnStartScheduleMeeting.setOnClickListener(view -> {
            mViewModel.createScheduleMeetingItem();
        });
    }

    @Override
    protected void initData() {
        if (dataList != null) {
            dataList.clear();
        }
        dataList.add(new ScheduleMeetingItem("会议主题", ScheduleMeetingItem.EDIT_TEXT_TITLE_ACTION));
        dataList.add(new ScheduleMeetingItem("开始时间", "请选择开始入会时间", ScheduleMeetingItem.SET_START_TIME_ACTION));
        dataList.add(new ScheduleMeetingItem("结束时间", "请选择会议结束时间", ScheduleMeetingItem.SET_END_TIME_ACTION));
        dataList.add(new ScheduleMeetingItem("会议密码", false, ScheduleMeetingItem.ENABLE_MEETING_PWD_ACTION));

        dataList.add(new ScheduleMeetingItem("自动静音", "参会者加入会议时自动静音", false, ScheduleMeetingItem.SET_AUDIO_MUTE_ACTION));
        dataList.add(new ScheduleMeetingItem("允许自行解除静音", "参会者被自动静音后可自行打开", true, ScheduleMeetingItem.SET_ALLOW_AUDIO_ON_ACTION));

        dataList.add(new ScheduleMeetingItem("自动关闭视频", "参会者加入会议时自动关闭视频", false, ScheduleMeetingItem.SET_VIDEO_MUTE_ACTION));
        dataList.add(new ScheduleMeetingItem("允许自行打开视频", "参会者被自动关闭视频后可自行打开", true, ScheduleMeetingItem.SET_ALLOW_VIDEO_ON_ACTION));

        settingsService = NEMeetingKit.getInstance().getSettingsService();
        if(settingsService.isMeetingLiveEnabled()){
            dataList.add(new ScheduleMeetingItem("开启直播", false, ScheduleMeetingItem.ENABLE_MEETING_LIVE_ACTION));
        }
        dataList.add(new ScheduleMeetingItem("开启录制", false, ScheduleMeetingItem.ENABLE_MEETING_RECORD_ACTION));
        dataList.add(new ScheduleMeetingItem("扩展字段", ScheduleMeetingItem.SET_EXTRA_DATA_ACTION));
        mAdapter.resetData(dataList);

        Intent intent = getActivity().getIntent();
        String strScene = intent.getStringExtra("scene");
        if (!TextUtils.isEmpty(strScene)) {
            try {
                jsonScene = new JSONObject(strScene);
            }catch (Exception e){
                Toast.makeText(this.getContext(),"sence params is error",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CalendarUtil.closeOptionsMenu();
    }
}
