/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.demo.adapter.ScheduleMeetingDetailAdapter;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.data.MeetingItem;
import com.netease.meetinglib.demo.data.ScheduleMeetingDetailItem;
import com.netease.meetinglib.demo.databinding.FragmentScheduleDatailBinding;
import com.netease.meetinglib.demo.viewmodel.ScheduleDetailViewModel;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingItemStatus;
import com.netease.meetinglib.sdk.NEMeetingLiveAuthLevel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;


public class ScheduleMeetingDetailFragment extends BaseFragment<FragmentScheduleDatailBinding> {

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
    protected FragmentScheduleDatailBinding getViewBinding() {
        return FragmentScheduleDatailBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(ScheduleDetailViewModel.class);
        mAdapter = new ScheduleMeetingDetailAdapter(getActivity(), new ArrayList<>());
        binding.rvScheduleMeeting.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter.setHasStableIds(true);
        binding.rvScheduleMeeting.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, position) -> {
            ScheduleMeetingDetailItem item = dataList.get(position);

        });
        mAdapter.setOnChildClickListener((clickAction) -> {
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
        binding.btnJoinScheduleMeeting.setOnClickListener(view -> {
            NEJoinMeetingParams params = new NEJoinMeetingParams();
            params.meetingId = item.getMeetingId();
            params.password = item.getPassword();
            params.displayName = SdkAuthenticator.getAccount();
            mViewModel.joinMeeting(params, null, new ToastCallback<>(getActivity(), "scheduleMeeting"));
        });
        binding.btnCancelScheduleMeeting.setOnClickListener(view -> {
            mViewModel.cancelMeeting(item.getMeetingUniqueId(),
                                     new ToastCallback<Void>(getActivity(), "scheduleMeeting") {

                                         @Override
                                         public void onResult(int resultCode, String resultMsg, Void resultData) {
                                             super.onResult(resultCode, resultMsg, resultData);
                                             if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                                                 Navigation.findNavController(getView()).popBackStack();
                                             }
                                         }
                                     });
        });
    }

    private void copyValue(String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
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
        dataList.add(new ScheduleMeetingDetailItem("会议ID", item.getMeetingId(), "复制",
                                                   ScheduleMeetingDetailItem.COPY_MEETING_ID_ACTION));
        dataList.add(new ScheduleMeetingDetailItem("开始时间", "", String.valueOf(item.getStartTime()), 0));
        dataList.add(new ScheduleMeetingDetailItem("结束时间", "", String.valueOf(item.getEndTime()), 0));
        if (!TextUtils.isEmpty(item.getPassword())) {
            dataList.add(new ScheduleMeetingDetailItem("会议密码", item.getPassword(), "复制",
                                                       ScheduleMeetingDetailItem.COPY_PASSWORD_ACTION));
        }
        if (item.getLive() != null &&item.getLive().isEnable() && !TextUtils.isEmpty(item.getLive().liveUrl())) {
            dataList.add(new ScheduleMeetingDetailItem("直播地址", item.getLive().liveUrl(), "复制",
                                                       ScheduleMeetingDetailItem.COPY_LIVE_URL_ACTION));
        }
        if (item.getLive() != null &&item.getLive().isEnable() && item.getLive().getLiveWebAccessControlLevel() ==
                                                                  NEMeetingLiveAuthLevel.appToken) {
            dataList.add(new ScheduleMeetingDetailItem("直播模式", "", "仅本企业员工可观看",
                                                       ScheduleMeetingDetailItem.COPY_LIVE_LEVEL_ACTION));
        }
        NEMeetingItemStatus status = item.getStatus();
        if (status == NEMeetingItemStatus.cancel || status == NEMeetingItemStatus.recycled) {
            binding.btnCancelScheduleMeeting.setVisibility(View.GONE);
            binding.btnJoinScheduleMeeting.setVisibility(View.GONE);
        } else if (status == NEMeetingItemStatus.ended) {
            binding.btnCancelScheduleMeeting.setVisibility(View.GONE);
        } else if (status == NEMeetingItemStatus.init || status == NEMeetingItemStatus.started) {
            binding.btnCancelScheduleMeeting.setVisibility(View.VISIBLE);
            binding.btnJoinScheduleMeeting.setVisibility(View.VISIBLE);
        }
        mAdapter.resetData(dataList);
    }
}
