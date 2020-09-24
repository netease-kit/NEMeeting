/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.Manifest;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.netease.meetinglib.demo.base.BaseActivity;
import com.netease.meetinglib.demo.databinding.ActivityMainBinding;
import com.netease.meetinglib.demo.viewmodel.MainViewModel;
import com.netease.meetinglib.sdk.NEMeetingInfo;
import com.netease.meetinglib.sdk.NEMeetingMenuItem;
import com.netease.meetinglib.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.netease.meetinglib.sdk.control.NEControlListener;
import com.netease.meetinglib.sdk.control.NEControlMenuItem;
import com.netease.meetinglib.sdk.control.NEControlMenuItemClickListener;
import com.netease.meetinglib.sdk.control.NEControlResult;
import com.permissionx.guolindev.PermissionX;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainViewModel mViewModel;

    @Override
    protected void onRestart() {
        super.onRestart();
        NEMeetingService meetingService = mViewModel.getMeetingService();
        if (meetingService != null && meetingService.getMeetingStatus()
                == NEMeetingStatus.MEETING_STATUS_INMEETING) {
            meetingService.returnToMeeting(this);
        }
    }

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //return to meeting if it is in-meeting
        NEMeetingService meetingService = mViewModel.getMeetingService();
        if (meetingService != null && meetingService.getMeetingStatus()
                == NEMeetingStatus.MEETING_STATUS_INMEETING) {
            meetingService.returnToMeeting(this);
            finish();
            return;
        }

        requestPermissions();

        mViewModel.observeStateLiveData(this, state -> {
            if (state == SdkAuthenticator.AuthStateChangeListener.AUTHORIZED) {
                dissMissDialogProgress();
                Navigation.findNavController(MainActivity.this, R.id.fragment).navigate(R.id.homeFragment);
            } else if (state == SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE) {
                dissMissDialogProgress();
                Navigation.findNavController(MainActivity.this, R.id.fragment).navigate(R.id.entranceFragment);
            } else {
                showDialogProgress(getString(R.string.login));
            }
        });

        SdkInitializer.getInstance().addListener(this::onInitialized);
        setupMeetingMinimizedLayout();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }


    private void requestPermissions() {
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白"))
                .onForwardToSettings((scope, deniedList) -> scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白"))
                .request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast.makeText(MainActivity.this, "应用未被授予以下权限：" + deniedList, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onInitialized(int total) {
        mViewModel.setOnInjectedMenuItemClickListener(new OnCustomMenuListener());
        mViewModel.setOnControlCustomMenuItemClickListener(new OnControlCustomMenuListener());
        mViewModel.registerControlListener(controlListener);
    }

    NEControlListener controlListener = new NEControlListener() {
        @Override
        public void onStartMeetingResult(NEControlResult status) {
            Toast.makeText(MainActivity.this, "遥控器开始会议事件回调:" + status.code + "#" + status.message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onJoinMeetingResult(NEControlResult status) {
            Toast.makeText(MainActivity.this, "遥控器加入会议事件回调:" + status.code + "#" + status.message, Toast.LENGTH_SHORT).show();
        }
    };

    public class OnCustomMenuListener implements NEMeetingOnInjectedMenuItemClickListener {
        @Override
        public void onInjectedMenuItemClick(NEMeetingMenuItem menuItem, NEMeetingInfo meetingInfo) {
            switch (menuItem.itemId) {
                case 101:
                    NEMeetingSDK.getInstance().getMeetingService().getCurrentMeetingInfo((resultCode, resultMsg, resultData) -> {
                        Toast.makeText(MainActivity.this, "获取房间信息NEMeetingInfo:" + resultData.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("OnCustomMenuListener", "getCurrentMeetingInfo:resultCode " + resultCode + "#resultData " + resultData.toString());
                    });
                    break;
                default:
                    Toast.makeText(MainActivity.this, "点击事件Id:" + menuItem.itemId + "#点击事件tittle:" + menuItem.itemId, Toast.LENGTH_SHORT).show();
                    break;

            }
            Log.d("OnCustomMenuListener", "onInjectedMenuItemClicked:menuItem " + menuItem.toString() + "#" + meetingInfo.toString());
        }
    }

    public class OnControlCustomMenuListener implements NEControlMenuItemClickListener {


        @Override
        public void onSettingMenuItemClick(NEControlMenuItem menuItem) {
            Toast.makeText(MainActivity.this, "点击了" + menuItem.title, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShareMenuItemClick(NEControlMenuItem menuItem, NEMeetingInfo meetingInfo) {
            Toast.makeText(MainActivity.this, "点击了" + menuItem.title, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        return NavHostFragment.findNavController(fragment).navigateUp();
    }

    void setupMeetingMinimizedLayout() {
        binding.meetingMinimizedLayout.setOnClickListener(v -> NEMeetingSDK.getInstance().getMeetingService().returnToMeeting(this));
        mViewModel.getMeetingMinimizedLiveData().observe(this, this::toggleMeetingMinimizedView);
    }

    private void toggleMeetingMinimizedView(boolean show) {
        int dx = getResources().getDimensionPixelSize(R.dimen.meeting_minimized_layout_size);
        if (show) {
            mViewModel.getMeetingTimeLiveData().observe(this, this::updateMeetingTime);
        } else {
            mViewModel.getMeetingTimeLiveData().removeObserver(this::updateMeetingTime);
        }
        binding.meetingMinimizedLayout.animate()
                .x(show ? 0 : -dx)
                .setDuration(1000)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void updateMeetingTime(String timeText) {
        binding.meetingTime.setText(timeText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewModel != null) {
            mViewModel.unRegisterControlListener(controlListener);
        }
    }
}
