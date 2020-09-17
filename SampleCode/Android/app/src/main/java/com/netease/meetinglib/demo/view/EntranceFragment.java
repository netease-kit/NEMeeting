/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;



import androidx.navigation.Navigation;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.databinding.FragmentEntranceBinding;

public class EntranceFragment extends BaseFragment<FragmentEntranceBinding> {

    @Override
    protected void initView() {

        setHandleOnBackDesktopPressed(true);
        binding.btnLogin.setOnClickListener(v ->
                Navigation.findNavController(getView()).navigate(R.id.action_entranceFragment_to_loginFragment)
        );

        binding.btnJoinMeeting.setOnClickListener(v ->
                Navigation.findNavController(getView()).navigate(R.id.action_entranceFragment_to_joinMeetingFragment)
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
