/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.netease.meetinglib.demo.fragment.InitFragment;
import com.netease.meetinglib.demo.fragment.JoinMeetingFragment;
import com.netease.meetinglib.demo.fragment.LoadingFragment;
import com.netease.meetinglib.demo.fragment.LoginFragment;
import com.netease.meetinglib.demo.fragment.LoginedFragment;
import com.netease.meetinglib.demo.fragment.StartMeetingFragment;
import com.permissionx.guolindev.PermissionX;

public class MainActivity extends AppCompatActivity implements ContentSwitcher{

    private static final String TAG = "MainActivity";

    private boolean stateLoss = false;
    LoadingFragment loadingFragment;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                switchContent((String)msg.obj);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        /*
        if (isInMeeting()) {
            //Todo: return to meeting here
            finish();
            return;
        }*/

        setContentView(R.layout.activity_main);

        SdkAuthenticator.getInstance().setAuthStateChangeListener(state -> {
            dismissLoading();
            if (state == SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE) {
                switchContent(Constants.CONTENT_ID_INIT);
            } else if (state == SdkAuthenticator.AuthStateChangeListener.AUTHORIZED){
                switchContent(Constants.CONTENT_ID_LOGINED);
            } else {
                showLoading("玩命登录中...");
            }
        });
        if (savedInstanceState == null) {
            switchContent(SdkAuthenticator.getInstance().isAuthorized() ? Constants.CONTENT_ID_LOGINED : Constants.CONTENT_ID_INIT);
        }
        requestPermissions();
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

    @Override
    public void switchContent(String contentId) {
        handler.removeMessages(1);
        if (stateLoss) {
            handler.sendMessageDelayed(handler.obtainMessage(1, contentId), 100);
            return;
        }

        Log.i(TAG, "switch content: " + contentId);
        if (!TextUtils.isEmpty(contentId)) {
            Fragment target = getSupportFragmentManager().findFragmentByTag(contentId);
            if (target != null) return;

            boolean addToBackStack = false;

            switch (contentId) {
                case Constants.CONTENT_ID_INIT:
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    target = new InitFragment();
                    break;
                case Constants.CONTENT_ID_LOGIN:
                    target = new LoginFragment();
                    addToBackStack = true;
                    break;
                case Constants.CONTENT_ID_LOGINED:
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    target = new LoginedFragment();
                    break;
                case Constants.CONTENT_ID_START_MEETING:
                    target = new StartMeetingFragment();
                    addToBackStack = true;
                    break;
                case Constants.CONTENT_ID_JOIN_MEETING:
                case Constants.CONTENT_ID_JOIN_MEETING_ANONYMOUS:
                    target = new JoinMeetingFragment();
                    addToBackStack = true;
                    break;
            }

            if (target != null) {
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.content, target, contentId);
                if (addToBackStack) {
                    transaction.addToBackStack(contentId);
                }
                transaction.commit();
            }
        }
    }

    private void showLoading(String msg) {
        if (loadingFragment != null) {
            loadingFragment.setMessage(msg);
        } else {
            loadingFragment = LoadingFragment.newInstance(msg);
            loadingFragment.setCancelable(false);
            loadingFragment.showNow(getSupportFragmentManager(), "dialog");
        }
    }

    private void dismissLoading() {
        if (loadingFragment != null) {
            loadingFragment.dismiss();
            loadingFragment = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stateLoss = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLoss = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        SdkAuthenticator.getInstance().setAuthStateChangeListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.meeting_settings) {
            MeetingSettingsActivity.start(this);
        }
        return true;
    }
}
