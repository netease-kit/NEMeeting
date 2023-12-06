// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingParams;
import com.netease.yunxin.kit.meeting.sdk.NEScreenSharingStatus;

public class ScreenSharingViewModel extends AndroidViewModel {
  private Context context;

  public ScreenSharingViewModel(Application application) {
    super(application);
    this.context = application.getApplicationContext();
  }

  public MutableLiveData<NEScreenSharingStatus> screenStatus =
      new MutableLiveData<>(NEScreenSharingStatus.SCREEN_SHARING_STATUS_IDLE);

  private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

  public void startScreenSharing(
      Intent data,
      @NonNull NEScreenSharingParams param,
      @Nullable NEScreenSharingOptions opts,
      NECallback<Void> callback) {
    mRepository.startScreenSharing(context, data, param, opts, callback);
  }

  public void stopScreenShare(NECallback<Void> callback) {
    mRepository.stopScreenShare(context, callback);
  }

  public void observeScreenStatus(LifecycleOwner owner, Observer<NEScreenSharingStatus> observer) {
    screenStatus.observe(owner, observer);
  }

  public void addScreenSharingStatusListener() {
    mRepository.addScreenSharingStatusListener(
        event -> {
          screenStatus.setValue(event.status);
          Toast.makeText(context, "屏幕共享状态变更: " + event.status.name(), Toast.LENGTH_SHORT).show();
        });
  }
}
