/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProviders;

import com.netease.yunxin.kit.meeting.sampleapp.MeetingSettingsActivity;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.menu.InjectMenuContainer;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.SettingsViewModel;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentSettingBinding;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMeetingMenuItem;

import java.util.List;


public class SettingsFragment extends BaseFragment<FragmentSettingBinding> {
    private SettingsViewModel mViewModel;

    private List<NEMeetingMenuItem> toolbarMenu;
    ActivityResultLauncher<Intent> configToolbarMenuResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) toolbarMenu = InjectMenuContainer.getSelectedMenu();
            });

    private List<NEMeetingMenuItem> moreMenu;
    ActivityResultLauncher<Intent> configMoreMenuResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) moreMenu = InjectMenuContainer.getSelectedMenu();
            });

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        mViewModel.getBeautyFaceValue((resultCode, resultMsg, resultData) -> Log.d("SettingsFragment", "initView:getBeautyFaceValue =  " +resultData ));

        binding.btnLogout.setOnClickListener(view -> SdkAuthenticator.getInstance().logout(true));
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
