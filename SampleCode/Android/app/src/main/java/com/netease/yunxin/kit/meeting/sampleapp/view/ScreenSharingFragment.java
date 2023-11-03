// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.text.Editable;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import com.netease.yunxin.kit.meeting.sampleapp.base.BaseFragment;
import com.netease.yunxin.kit.meeting.sampleapp.databinding.FragmentScreenShareBinding;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.viewmodel.ScreenSharingViewModel;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingParams;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingStatus;
import java.util.Objects;

public class ScreenSharingFragment extends BaseFragment<FragmentScreenShareBinding> {

  private static final String TAG = ScreenSharingFragment.class.getSimpleName();
  private ScreenSharingViewModel mViewModel;
  private int screenShareRequestCode = 1002;

  @Override
  protected FragmentScreenShareBinding getViewBinding() {
    return FragmentScreenShareBinding.inflate(getLayoutInflater());
  }

  @Override
  protected void initView() {
    mViewModel = ViewModelProviders.of(this).get(ScreenSharingViewModel.class);
    mViewModel.addScreenSharingStatusListener();
    binding.btnScreenSharing.setOnClickListener(
        view -> {
          if (Objects.equals(
              NEScreenSharingStatus.SCREEN_SHARING_STATUS_IDLE,
              mViewModel.screenStatus.getValue())) {

            MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager)
                    getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, screenShareRequestCode);
          } else if (Objects.equals(
                  NEScreenSharingStatus.SCREEN_SHARING_STATUS_STARTED,
                  mViewModel.screenStatus.getValue())
              || Objects.equals(
                  NEScreenSharingStatus.SCREEN_SHARING_STATUS_WAITING,
                  mViewModel.screenStatus.getValue())) {
            mViewModel.stopScreenShare(
                (resultCode, resultMsg, resultData) -> {
                  LogUtil.log(TAG, "stopScreenShare resultCode" + resultCode);
                });
          }
        });
    mViewModel.observeScreenStatus(
        this,
        status -> {
          //            boolean isScreenSharingStarted = Objects.equals(NEScreenSharingStatus.SCREEN_SHARING_STATUS_STARTED, );
          binding.btnScreenSharing.setText(mViewModel.screenStatus.getValue().name());
        });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    LogUtil.log(TAG, "onActivityResult resultCode" + resultCode + "requestCode" + requestCode);
    if (requestCode == screenShareRequestCode && resultCode == Activity.RESULT_OK && data != null) {

      NEScreenSharingParams params = new NEScreenSharingParams();
      Editable editableName = binding.edtScreenSharingName.getText();
      Editable editableCode = binding.edtScreenSharingCode.getText();
      if (editableName != null && editableCode != null) {
        params.displayName = editableName.toString();
        params.sharingCode = editableCode.toString();
      }
      NEScreenSharingOptions options = new NEScreenSharingOptions();
      options.enableAudioShare = binding.checkboxScreenShareAudio.isChecked();
      mViewModel.startScreenSharing(
          data,
          params,
          options,
          (code, msg, resultData) -> {
            if (msg != null) {
              Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
          });
    }
  }

  @Override
  protected void initData() {}

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
