// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseActivity;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.ActivityMainBinding;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.nim.NIMLoginActivity;
import com.netease.yunxin.kit.meeting.sampleapp.utils.AlertDialogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.MainViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingInfo;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingService;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatus;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuClickInfo;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuStateController;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

  private final String TAG = MainActivity.class.getSimpleName() + '@' + hashCode();

  private MainViewModel mViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkAppKey();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    NEMeetingService meetingService = mViewModel.getMeetingService();
    if (meetingService != null
        && meetingService.getMeetingStatus() == NEMeetingStatus.MEETING_STATUS_INMEETING) {
      meetingService.returnToMeeting(this);
      LogUtil.log(TAG, "onRestart returnToMeeting");
    }
  }

  @Override
  protected void initView() {
    mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    //return to meeting if it is in-meeting
    NEMeetingService meetingService = mViewModel.getMeetingService();
    if (meetingService != null
        && meetingService.getMeetingStatus() == NEMeetingStatus.MEETING_STATUS_INMEETING) {
      meetingService.returnToMeeting(this);
      LogUtil.log(TAG, "initView returnToMeeting");
      finish();
      return;
    }

    // requestPermissions();

    mViewModel.observeStateLiveData(
        this,
        state -> {
          if (state == SdkAuthenticator.AuthStateChangeListener.AUTHORIZED) {
            dissMissDialogProgress();
            Navigation.findNavController(MainActivity.this, R.id.fragment)
                .navigate(R.id.homeFragment);
          } else if (state == SdkAuthenticator.AuthStateChangeListener.UN_AUTHORIZE) {
            dissMissDialogProgress();
            Navigation.findNavController(MainActivity.this, R.id.fragment)
                .navigate(R.id.entranceFragment);
          } else if (state == SdkAuthenticator.AuthStateChangeListener.AUTHORIZING) {
            showDialogProgress(getString(R.string.login));
          } else if (state == SdkAuthenticator.AuthStateChangeListener.AUTHOR_FAIL) {
            dissMissDialogProgress();
          }
        });

    SdkInitializer.getInstance().addListener(this::onInitialized);
    setupMeetingMinimizedLayout();
  }

  @Override
  protected void initData() {}

  @Override
  protected ActivityMainBinding getViewBinding() {
    return ActivityMainBinding.inflate(getLayoutInflater());
  }

  private void onInitialized(int initializeIndex) {
    mViewModel.setOnInjectedMenuItemClickListener(new OnCustomMenuListener(mViewModel));
  }

  public class OnCustomMenuListener implements NEMeetingOnInjectedMenuItemClickListener {

    private final MainViewModel viewModel;

    public OnCustomMenuListener(MainViewModel viewModel) {
      this.viewModel = viewModel;
    }

    @Override
    public void onInjectedMenuItemClick(
        Context context,
        NEMenuClickInfo clickInfo,
        NEMeetingInfo meetingInfo,
        NEMenuStateController stateController) {
      Log.d(
          "OnCustomMenuListener",
          "onInjectedMenuItemClicked:menuItem " + clickInfo + "#" + meetingInfo.toString());
      if (clickInfo.getItemId() == 100) {
        MeetingSettingsActivity.start(context);
      } else if (clickInfo.getItemId() == 101) {
        minimizeCurrentMeeting();
      } else {
        AlertDialogUtil.setAlertDialog(
            new AlertDialog.Builder(context)
                .setTitle("菜单项被点击了")
                .setMessage(clickInfo + "\n" + meetingInfo)
                .setPositiveButton(
                    "确定", (dialog, which) -> didMenuItemStateTransition(stateController, true))
                .setNegativeButton(
                    "取消", (dialog, which) -> didMenuItemStateTransition(stateController, false))
                .setNeutralButton("忽略", (dialog, which) -> {})
                .setCancelable(false)
                .create());
        if (AlertDialogUtil.getAlertDialog() != null) {
          AlertDialogUtil.getAlertDialog().show();
        }
      }
    }

    private void didMenuItemStateTransition(
        NEMenuStateController controller, boolean didTransition) {
      if (controller != null) {
        controller.didStateTransition(didTransition, null);
      }
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
      case R.id.app_settings:
        AppSettingsActivity.start(this);
        break;
      case R.id.im_login:
        NIMLoginActivity.start(this);
        break;
      case R.id.minimize_meeting:
        minimizeCurrentMeeting();
        break;
      case R.id.leave_meeting:
        leaveCurrentMeeting(false);
        break;
      case R.id.close_meeting:
        leaveCurrentMeeting(true);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  private void minimizeCurrentMeeting() {
    NEMeetingService meetingService = NEMeetingKit.getInstance().getMeetingService();
    if (meetingService != null) {
      meetingService.minimizeCurrentMeeting(new ToastCallback<>(this, "最小化会议"));
    }
  }

  private void leaveCurrentMeeting(boolean closeIfHost) {
    NEMeetingService meetingService = NEMeetingKit.getInstance().getMeetingService();
    if (meetingService != null) {
      meetingService.leaveCurrentMeeting(
          closeIfHost, new ToastCallback<>(this, String.format("%s会议", closeIfHost ? "结束" : "离开")));
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
    return NavHostFragment.findNavController(fragment).navigateUp();
  }

  void setupMeetingMinimizedLayout() {
    binding.meetingMinimizedLayout.setOnClickListener(
        v -> NEMeetingKit.getInstance().getMeetingService().returnToMeeting(this));
    mViewModel.getMeetingMinimizedLiveData().observe(this, this::toggleMeetingMinimizedView);
  }

  private void toggleMeetingMinimizedView(boolean show) {
    LogUtil.log(
        TAG, "toggleMeetingMinimizedView: " + show + "==" + binding.meetingMinimizedLayout.getX());
    int dx = getResources().getDimensionPixelSize(R.dimen.meeting_minimized_layout_size);
    if (show) {
      mViewModel.getMeetingTimeLiveData().observe(this, this::updateMeetingTime);
    } else {
      mViewModel.getMeetingTimeLiveData().removeObserver(this::updateMeetingTime);
    }
    binding
        .meetingMinimizedLayout
        .animate()
        .x(show ? 0 : -dx)
        .setDuration(1000)
        .setInterpolator(new OvershootInterpolator())
        .start();
  }

  private void updateMeetingTime(String timeText) {
    binding.meetingTime.setText(timeText);
  }

  private void checkAppKey() {
    String appKey = MeetingApplication.getInstance().getServerConfig().getAppKey();
    if ("Your AppKey".equals(appKey)) {
    new AlertDialog.Builder(this)
            .setTitle("检测到AppKey未设置")
            .setMessage("请在'appkey.xml'文件中填入正确的AppKey")
            .setPositiveButton("OK", null)
            .setCancelable(false)
            .create()
            .show();
    }
  }
}
