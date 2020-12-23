/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;


import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.netease.meetinglib.demo.MeetingSettingsActivity;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.databinding.FragmentSettingBinding;
import com.netease.meetinglib.demo.viewmodel.SettingsViewModel;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.control.NEControlParams;


public class SettingsFragment extends BaseFragment<FragmentSettingBinding> {
    private SettingsViewModel mViewModel;

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        mViewModel.getBeautyFaceValue((resultCode, resultMsg, resultData) -> Log.d("SettingsFragment", "initView:getBeautyFaceValue =  " +resultData ));

        binding.btnLogout.setOnClickListener(view -> SdkAuthenticator.getInstance().logout(true));
        binding.btnOpenControl.setOnClickListener(v -> mViewModel.openController(new NEControlParams(SdkAuthenticator.getAccount()), null, this::onOpenControllerCallBack));
        binding.btnOpenBeauty.setOnClickListener(v -> mViewModel.openBeautyUI((resultCode, resultMsg, resultData) -> Toast.makeText(getActivity(), "进入美颜预览#" + resultMsg, Toast.LENGTH_SHORT).show()));
        binding.btnMeetingSettings.setOnClickListener(v -> MeetingSettingsActivity.start(getActivity()));
    }

    @Override
    protected void initData() {

    }

    private void onOpenControllerCallBack(int resultCode, String resultMsg, Object resultData) {
        if (resultCode != NEMeetingError.ERROR_CODE_SUCCESS) {
            Toast.makeText(getActivity(), "进入遥控器失败#" + resultMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected FragmentSettingBinding getViewBinding() {
        return FragmentSettingBinding.inflate(getLayoutInflater());
    }
}
