/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.view;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProviders;

import com.netease.meetinglib.demo.MeetingSettingsActivity;
import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.demo.SdkAuthenticator;
import com.netease.meetinglib.demo.base.BaseFragment;
import com.netease.meetinglib.demo.databinding.FragmentSettingBinding;
import com.netease.meetinglib.demo.menu.ControllerInjectMenuArrangeActivity;
import com.netease.meetinglib.demo.menu.InjectMenuArrangeActivity;
import com.netease.meetinglib.demo.menu.InjectMenuContainer;
import com.netease.meetinglib.demo.viewmodel.SettingsViewModel;
import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.control.NEControlOptions;
import com.netease.meetinglib.sdk.control.NEControlParams;
import com.netease.meetinglib.sdk.menu.NEMeetingMenuItem;

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
        binding.btnOpenControl.setOnClickListener(v -> {
            NEControlOptions opts = new NEControlOptions();
            opts.fullToolbarMenuItems = toolbarMenu;
            opts.fullMoreMenuItems = moreMenu;
            mViewModel.openController(new NEControlParams(SdkAuthenticator.getAccount()),
                                                                                 opts, this::onOpenControllerCallBack);});
        binding.btnOpenBeauty.setOnClickListener(v -> mViewModel.openBeautyUI((resultCode, resultMsg, resultData) -> Toast.makeText(getActivity(), "进入美颜预览#" + resultMsg, Toast.LENGTH_SHORT).show()));
        binding.btnMeetingSettings.setOnClickListener(v -> MeetingSettingsActivity.start(getActivity()));

        binding.configToolbarMenus.setOnClickListener(v -> {
            InjectMenuContainer.setSelectedMenu(toolbarMenu);
            configToolbarMenuResult.launch(new Intent(getActivity(), ControllerInjectMenuArrangeActivity.class));
        });

        binding.configMoreMenus.setOnClickListener(v -> {
            InjectMenuContainer.setSelectedMenu(moreMenu);
            configMoreMenuResult.launch(new Intent(getActivity(), ControllerInjectMenuArrangeActivity.class));
        });
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
