/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;


import androidx.navigation.Navigation;

import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.databinding.FragmentSettingBinding;


public class SettingsFragment extends BaseFragment<FragmentSettingBinding> {

    @Override
    protected void initView() {
        binding.btnLogout.setOnClickListener(view -> SdkAuthenticator.getInstance().logout(true));
        SdkAuthenticator.getInstance().setAuthStateChangeListener(state -> {
            if (state == SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE) {
                Navigation.findNavController(getView()).navigateUp();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected FragmentSettingBinding getViewBinding() {
        return FragmentSettingBinding.inflate(getLayoutInflater());
    }
}
