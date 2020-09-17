/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.netease.meetinglib.demo.base.BaseActivity;
import com.netease.meetinglib.demo.viewmodel.MainViewModel;
import com.netease.meetinglib.sdk.NEMeetingInfo;
import com.netease.meetinglib.sdk.NEMeetingMenuItem;
import com.netease.meetinglib.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.permissionx.guolindev.PermissionX;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
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
        SdkAuthenticator.getInstance().setAuthStateChangeListener(state -> {
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
        SdkInitializer.getInstance().addListener(total -> mViewModel.setOnInjectedMenuItemClickListener(new OnCustomMenuListener()));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!SdkAuthenticator.getInstance().isAuthorized()) {
            Navigation.findNavController(MainActivity.this, R.id.fragment).navigate(R.id.entranceFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.meeting_settings:
                MeetingSettingsActivity.start(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        return NavHostFragment.findNavController(fragment).navigateUp();
    }
}
