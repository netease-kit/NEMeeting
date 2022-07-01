/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;



import androidx.navigation.Navigation;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentEntranceBinding;

public class EntranceFragment extends BaseFragment<FragmentEntranceBinding> {

    @Override
    protected void initView() {

        setHandleOnBackDesktopPressed(true);

        binding.btnLoginWithNeMeeting.setOnClickListener(v ->
                Navigation.findNavController(getView()).navigate(R.id.action_entranceFragment_to_loginWithNEMeetingFragment)
        );

        binding.btnLoginWithToken.setOnClickListener(v ->
                Navigation.findNavController(getView()).navigate(R.id.action_entranceFragment_to_loginWithTokenFragment)
        );

    }

    @Override
    protected void initData() {
    }

    @Override
    protected FragmentEntranceBinding getViewBinding() {
        return FragmentEntranceBinding.inflate(getLayoutInflater());
    }
}
