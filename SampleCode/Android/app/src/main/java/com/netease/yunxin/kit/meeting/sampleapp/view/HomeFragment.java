// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.adapter.MeetingListAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingItem;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentHomeBinding;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.HomeViewModel;
import java.util.ArrayList;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
  private static final String TAG = HomeFragment.class.getSimpleName();
  private HomeViewModel mViewModel;
  private MeetingListAdapter mAdapter;

  @Override
  protected void initView() {
    mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    setHandleOnBackDesktopPressed(true);
    mAdapter = new MeetingListAdapter(new ArrayList<>());
    binding.rvMeetingList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    mAdapter.setHasStableIds(true);
    binding.rvMeetingList.setEmptyView(binding.imgEmpty);
    binding.rvMeetingList.setAdapter(mAdapter);

    mAdapter.setOnItemClickListener(
        (view, position) -> {
          MeetingItem item = mAdapter.getData().get(position);
          Bundle bundle = new Bundle();
          bundle.putSerializable("meetingItem", item);
          Navigation.findNavController(getView())
              .navigate(R.id.action_homeFragment_to_scheduleMeetingDetailFragment, bundle);
        });

    mViewModel.observeMeetingItems(
        this,
        neMeetingItems -> {
          if (neMeetingItems != null) {
            mAdapter.resetData(neMeetingItems);
          } else {
            mAdapter.clear();
          }
        });
    mViewModel.observeChangeMeetingItems(
        this,
        neMeetingItems -> {
          if (mAdapter.getData().size() <= 0) {
            mAdapter.resetData(neMeetingItems);
            return;
          }
          int len = mAdapter.getData().size();
          for (int i = 0; i < neMeetingItems.size(); i++) {
            for (int j = 0; j < len; j++) {
              MeetingItem changeItem = neMeetingItems.get(i);
              MeetingItem originalItem = mAdapter.getData().get(j);
              if (changeItem.getMeetingId() == originalItem.getMeetingId()) {
                switch (changeItem.getStatus()) {
                  case init:
                  case started:
                  case ended:
                    mAdapter.updateData(j, changeItem);
                    break;
                  default:
                    mAdapter.deleteItem(j);
                    break;
                }
              } else {
                switch (changeItem.getStatus()) {
                  case init:
                  case started:
                  case ended:
                    mAdapter.addNewData(j, changeItem);
                    break;
                  default:
                    break;
                }
              }
            }
          }
        });

    initListener();
  }

  @Override
  protected void initData() {
    if (mAdapter != null) {
      mAdapter.clear();
    }
    mViewModel.getMeetingList();
  }

  @Override
  protected FragmentHomeBinding getViewBinding() {
    return FragmentHomeBinding.inflate(getLayoutInflater());
  }

  private void initListener() {
    binding.btnStartMeeting.setOnClickListener(
        v ->
            Navigation.findNavController(getView())
                .navigate(R.id.action_homeFragment_to_startMeetingFragment));
    binding.btnJoinMeeting.setOnClickListener(
        v ->
            Navigation.findNavController(getView())
                .navigate(R.id.action_homeFragment_to_joinMeetingFragment));
    binding.btnScheduleMeeting.setOnClickListener(
        v ->
            Navigation.findNavController(getView())
                .navigate(R.id.action_homeFragment_to_scheduleMeetingFragment));
    binding.btnScreenSharing.setOnClickListener(
        v ->
            Navigation.findNavController(getView())
                .navigate(R.id.action_homeFragment_to_screenSharingFragment));
    binding.btnSetting.setOnClickListener(
        v ->
            Navigation.findNavController(getView())
                .navigate(R.id.action_homeFragment_to_settingsFragment));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mAdapter != null) {
      mAdapter.clear();
    }
  }
}
