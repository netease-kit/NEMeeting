// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseActivity;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.ActivityMainBinding;
import com.netease.yunxin.kit.meeting.sampleapp.dialog.ActionSheetDialog;
import com.netease.yunxin.kit.meeting.sampleapp.dialog.EditDialog;
import com.netease.yunxin.kit.meeting.sampleapp.dialog.ItemClickListerAdapter;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.utils.AlertDialogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.MainViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingInfo;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingService;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatus;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuClickInfo;
import com.netease.yunxin.kit.meeting.sdk.menu.NEMenuStateController;
import com.permissionx.guolindev.PermissionX;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

  private final String TAG = MainActivity.class.getSimpleName() + '@' + hashCode();

  private MainViewModel mViewModel;

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

    requestPermissions();

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

  private void requestPermissions() {
    PermissionX.init(this)
        .permissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
        .onExplainRequestReason(
            (scope, deniedList) ->
                scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白"))
        .onForwardToSettings(
            (scope, deniedList) ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白"))
        .request(
            (allGranted, grantedList, deniedList) -> {
              if (!allGranted) {
                Toast.makeText(MainActivity.this, "应用未被授予以下权限：" + deniedList, Toast.LENGTH_SHORT)
                    .show();
              }
            });
  }

  private void onInitialized(int initializeIndex) {
    mViewModel.setOnInjectedMenuItemClickListener(new OnCustomMenuListener(mViewModel));
  }

  public static class OnCustomMenuListener implements NEMeetingOnInjectedMenuItemClickListener {

    private MainViewModel viewModel;

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
      } else if (clickInfo.getItemId() == 102) {
        handleAudioManager(context);
      } else {
        AlertDialogUtil.setAlertDialog(
            new AlertDialog.Builder(context)
                .setTitle("菜单项被点击了")
                .setMessage(clickInfo.toString() + "\n" + meetingInfo.toString())
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

    private void handleAudioManager(Context context) {
      final ActionSheetDialog dialog = new ActionSheetDialog(context);
      dialog.setTitle(context.getString(R.string.audioSubscribeManager));
      dialog.addAction(0, context.getString(R.string.audioSubscribeAll));
      dialog.addAction(1, context.getString(R.string.audioUnSubscribeAll));
      dialog.addAction(2, context.getString(R.string.audioSubscribe));
      dialog.addAction(3, context.getString(R.string.audioUnSubscribe));
      dialog.addAction(4, context.getString(R.string.audioSubscribes));
      dialog.addAction(5, context.getString(R.string.audioUnSubscribes));
      dialog.setOnItemClickListener(
          new ItemClickListerAdapter<ActionSheetDialog.ActionItem>() {
            @Override
            public void onClick(View v, int pos, ActionSheetDialog.ActionItem data) {
              dialog.dismiss();
              switch (data.action) {
                case 0:
                  subscribeAllRemoteAudioStreams(context, true);
                  break;
                case 1:
                  subscribeAllRemoteAudioStreams(context, false);
                  break;
                case 2:
                  subscribeRemoteAudioStream(context, true);
                  break;
                case 3:
                  subscribeRemoteAudioStream(context, false);
                  break;
                case 4:
                  subscribeRemoteAudioStreams(context, true);
                  break;
                case 5:
                  subscribeRemoteAudioStreams(context, false);
                  break;
              }
            }
          });
      dialog.show();
    }

    private void subscribeAllRemoteAudioStreams(Context context, boolean subscribe) {
      viewModel.subscribeAllRemoteAudioStreams(
          subscribe,
          (resultCode, resultMsg, resultData) ->
              Toast.makeText(context, "code=" + resultCode + " msg" + resultMsg, Toast.LENGTH_LONG)
                  .show());
    }

    private void subscribeRemoteAudioStream(Context context, boolean subscribe) {
      EditDialog.show(
          context,
          context.getString(R.string.audioSubscribeManager),
          context.getString(R.string.inputSubscribeId),
          "请输入用户accountId",
          true,
          true,
          content -> {
            viewModel.subscribeRemoteAudioStream(
                content,
                subscribe,
                (resultCode, resultMsg, resultData) ->
                    Toast.makeText(
                            context, "code=" + resultCode + " msg" + resultMsg, Toast.LENGTH_LONG)
                        .show());
          });
    }

    private void subscribeRemoteAudioStreams(Context context, boolean subscribe) {
      EditDialog.show(
          context,
          context.getString(R.string.audioSubscribeManager),
          context.getString(R.string.inputSubscribeIds),
          "请输入用户accountId用英文逗号分割",
          true,
          true,
          content -> {
            List<String> ids = Arrays.asList(content.split(","));
            viewModel.subscribeRemoteAudioStreams(
                ids,
                subscribe,
                (resultCode, resultMsg, resultData) ->
                    Toast.makeText(
                            context,
                            "code="
                                + resultCode
                                + " msg"
                                + resultMsg
                                + " data "
                                + (resultData != null ? resultData.toString() : ""),
                            Toast.LENGTH_LONG)
                        .show());
          });
    }

    private static void didMenuItemStateTransition(
        NEMenuStateController controller, boolean didTransition) {
      if (controller != null) {
        controller.didStateTransition(didTransition, null);
      }
    }
  }

  public static class OnControlCustomInjectedMenuListener
      implements NEMeetingOnInjectedMenuItemClickListener {
    @Override
    public void onInjectedMenuItemClick(
        Context context,
        NEMenuClickInfo clickInfo,
        NEMeetingInfo meetingInfo,
        NEMenuStateController stateController) {
      Log.d(
          "OnCustomMenuListener",
          "onInjectedMenuItemClicked:menuItem " + clickInfo + "#" + meetingInfo.toString());
      AlertDialogUtil.setAlertDialog(
          new AlertDialog.Builder(context)
              .setTitle("菜单项被点击了")
              .setMessage(clickInfo.toString() + "\n" + meetingInfo.toString())
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

    private static void didMenuItemStateTransition(
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case R.id.app_settings:
        AppSettingsActivity.start(this);
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
}
