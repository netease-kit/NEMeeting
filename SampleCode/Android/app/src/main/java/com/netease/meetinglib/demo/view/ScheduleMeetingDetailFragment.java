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

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.ToastCallback;
import com.netease.meetinglib.demo.adapter.ScheduleMeetingDetailAdapter;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.data.ScheduleMeetingDetailItem;
import com.netease.meetinglib.demo.databinding.FragmentScheduleDatailBinding;
import com.netease.meetinglib.demo.viewmodel.ScheduleDetailViewModel;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingItemStatus;

import java.util.ArrayList;
import java.util.List;


public class ScheduleMeetingDetailFragment extends BaseFragment<FragmentScheduleDatailBinding> {
    private static final String TAG = ScheduleMeetingDetailFragment.class.getSimpleName();
    private List<ScheduleMeetingDetailItem> dataList = new ArrayList<>();
    private ScheduleMeetingDetailAdapter mAdapter;
    private ScheduleDetailViewModel mViewModel;
    private Bundle arguments;
    private String password;
    private String subject;
    private NEMeetingItemStatus status;
    private String meetingId;
    private long endTime;
    private long startTime;
    private long meetingUniqueId;

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
        binding.rvScheduleMeeting.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
                case ScheduleMeetingDetailItem.COPY_PASSWORD_ACTION:
                    copyValue(mAdapter.getData().get(clickAction).getDescription());
                    Toast.makeText(getActivity(), "密码复制成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        });


        binding.btnJoinScheduleMeeting.setOnClickListener(view -> {
            NEJoinMeetingParams params = new NEJoinMeetingParams();
            params.meetingId = meetingId;
            params.password = password;
            params.displayName = SdkAuthenticator.getAccount();
            mViewModel.joinMeeting(params, null, new ToastCallback<>(getActivity(), "scheduleMeeting"));
        });

        binding.btnCancelScheduleMeeting.setOnClickListener(view -> {
            mViewModel.cancelMeeting(meetingUniqueId, new ToastCallback<Void>(getActivity(), "scheduleMeeting") {
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
        meetingUniqueId = arguments.getLong("meetingUniqueId");
        password = arguments.getString("password");
        subject = arguments.getString("subject");
        meetingId = arguments.getString("meetingId");
        startTime = arguments.getLong("startTime");
        endTime = arguments.getLong("endTime");
        status = (NEMeetingItemStatus) arguments.getSerializable("status");

        if (dataList != null) {
            dataList.clear();
        }
        dataList.add(new ScheduleMeetingDetailItem(subject, "", "", 0));
        dataList.add(new ScheduleMeetingDetailItem("会议ID", meetingId, "复制", ScheduleMeetingDetailItem.COPY_MEETING_ID_ACTION));
        dataList.add(new ScheduleMeetingDetailItem("开始时间", "", String.valueOf(startTime), 0));
        dataList.add(new ScheduleMeetingDetailItem("结束时间", "", String.valueOf(endTime), 0));

        if (!TextUtils.isEmpty(password)) {
            dataList.add(new ScheduleMeetingDetailItem("会议密码", password, "复制", ScheduleMeetingDetailItem.COPY_PASSWORD_ACTION));
        }

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
