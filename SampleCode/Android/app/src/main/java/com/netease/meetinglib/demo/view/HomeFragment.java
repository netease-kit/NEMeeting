/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.adapter.MeetingListAdapter;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.data.MeetingItem;
import com.netease.meetinglib.demo.databinding.FragmentHomeBinding;
import com.netease.meetinglib.demo.viewmodel.HomeViewModel;
import com.netease.meetinglib.sdk.NEScheduleMeetingStatusListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private HomeViewModel mViewModel;
    private MeetingListAdapter mAdapter;
    private List<MeetingItem> dataList = new ArrayList<>();

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        setHandleOnBackDesktopPressed(true);
        mAdapter = new MeetingListAdapter(new ArrayList<>());
        binding.rvMeetingList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter.setHasStableIds(true);
        binding.rvMeetingList.setEmptyView(binding.imgEmpty);
        binding.rvMeetingList.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, position) -> {
            MeetingItem item = mAdapter.getData().get(position);
            Bundle bundle = new Bundle();
            bundle.putLong("meetingUniqueId", item.getMeetingUniqueId());
            bundle.putString("meetingId", String.valueOf(item.getMeetingId()));
            bundle.putLong("startTime", item.getStartTime());
            bundle.putLong("endTime", item.getEndTime());
            bundle.putString("password", item.getPassword());
            bundle.putString("subject", item.getSubject());
            bundle.putSerializable("status", item.getStatus());
            Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_scheduleMeetingDetailFragment, bundle);
        });

        mViewModel.observeMeetingItems(this, neMeetingItems -> {
            if (neMeetingItems != null) {
                mAdapter.resetData(neMeetingItems);
            }
        });

        initListener();
    }

    @Override
    protected void initData() {
        if (dataList != null) {
            dataList.clear();
        }
        mViewModel.getMeetingList();
    }

    @Override
    protected FragmentHomeBinding getViewBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());
    }

    private void initListener() {
        binding.btnStartMeeting.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_startMeetingFragment));
        binding.btnJoinMeeting.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_joinMeetingFragment));
        binding.btnScheduleMeeting.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_scheduleMeetingFragment));
        binding.btnLogout.setOnClickListener(v -> {
            SdkAuthenticator.getInstance().logout(true);
        });

    }
}
