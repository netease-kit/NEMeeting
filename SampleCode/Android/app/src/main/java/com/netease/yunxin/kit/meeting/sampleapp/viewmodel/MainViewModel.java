// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.netease.yunxin.kit.meeting.sampleapp.SdkAuthenticator;
import com.netease.yunxin.kit.meeting.sampleapp.SdkInitializer;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingDataRepository;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sdk.*;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingInviteStatus;
import java.util.List;

public class MainViewModel extends AndroidViewModel
    implements NEMeetingStatusListener,
        SdkInitializer.InitializeListener,
        SdkAuthenticator.AuthStateChangeListener,
        NEMeetingInviteStatusListener {

  private static final String TAG = "MainViewModel";

  private static final int MSG_ACTIVE = 1;

  private MeetingDataRepository mRepository = MeetingDataRepository.getInstance();

  private MutableLiveData<Integer> stateLiveData = new MutableLiveData<>();
  private MutableLiveData<Boolean> minimizedLiveData = new MutableLiveData<>();
  private MutableLiveData<String> meetingTimeLiveData =
      new MutableLiveData<String>() {
        @Override
        protected void onActive() {
          LogUtil.log(TAG, "meeting time livedata on active");
          super.onActive();
          handler.sendEmptyMessage(MSG_ACTIVE);
        }

        @Override
        protected void onInactive() {
          LogUtil.log(TAG, "meeting time livedata on inactive");
          handler.removeMessages(MSG_ACTIVE);
        }
      };
  private MutableLiveData<NEMeetingInfo> meetingInfoLiveData = new MutableLiveData<>();
  private long durationInitialTimestamp;

  private Context context;

  @SuppressLint("HandlerLeak")
  private Handler handler =
      new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
          if (msg.what == MSG_ACTIVE) {
            calculateElapsedTime();
            return;
          }
          super.handleMessage(msg);
        }
      };

  public MainViewModel(Application application) {
    super(application);
    SdkInitializer.getInstance().addListener(this);
    SdkAuthenticator.getInstance().setAuthStateChangeListener(this::onAuthStateChanged);
    this.context = application.getApplicationContext();
  }

  public MutableLiveData<Boolean> getMeetingMinimizedLiveData() {
    return minimizedLiveData;
  }

  public MutableLiveData<String> getMeetingTimeLiveData() {
    return meetingTimeLiveData;
  }

  public void observeStateLiveData(LifecycleOwner owner, Observer<Integer> observer) {
    stateLiveData.observe(owner, observer);
  }

  public void setOnInjectedMenuItemClickListener(
      NEMeetingOnInjectedMenuItemClickListener listener) {
    mRepository.setOnInjectedMenuItemClickListener(listener);
  }

  public NEMeetingService getMeetingService() {
    return mRepository.getMeetingService();
  }

  public NEMeetingInviteService getMeetingInviteService() {
    return mRepository.getMeetingInviteService();
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    NEMeetingService meetingService = getMeetingService();
    if (meetingService != null) {
      meetingService.removeMeetingStatusListener(this);
    }
    SdkInitializer.getInstance().removeListener(this);
    SdkAuthenticator.getInstance().setAuthStateChangeListener(null);
    if (handler != null) {
      handler.removeCallbacksAndMessages(null);
    }
  }

  @Override
  public void onInitialized(int initializeIndex) {
    getMeetingService().addMeetingStatusListener(this);
    getMeetingInviteService().addEventListener(this);
    NEMeetingStatus status = getMeetingService().getMeetingStatus();
    if (status != null) {
      reactToMeetingStatus(status);
    }
  }

  @Override
  public void onMeetingStatusChanged(Event event) {
    reactToMeetingStatus(event.status);
  }

  @Override
  public void onMeetingInviteStatusChanged(
      NEMeetingInviteStatus status, String meetingId, NEMeetingInviteInfo inviteInfo) {
    LogUtil.log(
        TAG, "onMeetingInviteStatusChanged: " + status + " " + meetingId + " " + inviteInfo);

    //    if (NEMeetingInviteStatus.MEETING_STATUS_CALLING == status) {
    //      NEJoinMeetingParams params = new NEJoinMeetingParams();
    //      params.meetingNum = inviteInfo.meetingNum;
    //      params.displayName = "customDisplayName";
    //      NEJoinMeetingOptions options =  new NEJoinMeetingOptions();
    //      options.noVideo = false;
    //      getMeetingInviteService()
    //          .acceptInvite(
    //              context,
    //              params,
    //              options,
    //              new NECallback<Void>() {
    //                @Override
    //                public void onResult(int resultCode, String resultMsg, Void resultData) {
    //                  LogUtil.log(TAG, "acceptInvite: " + resultCode + " " + resultMsg);
    //                }
    //              });
    //      getMeetingInviteService()
    //          .rejectInvite(
    //              event.meetingId,
    //              new NECallback<Void>() {
    //                @Override
    //                public void onResult(int resultCode, String resultMsg, Void resultData) {
    //                  LogUtil.log(TAG, "rejectInvite: " + resultCode + " " + resultMsg);
    //                }
    //              });
    //    }
  }

  private void reactToMeetingStatus(NEMeetingStatus status) {
    LogUtil.log(TAG, "onMeetingStatusChanged: " + status + "==" + minimizedLiveData.getValue());
    Boolean value =
        status == NEMeetingStatus.MEETING_STATUS_INMEETING_MINIMIZED
            || status == NEMeetingStatus.MEETING_STATUS_INMEETING
            || status == NEMeetingStatus.MEETING_STATUS_IN_WAITING_ROOM;
    if (minimizedLiveData.getValue() != value) {
      minimizedLiveData.setValue(value);
    }
    LogUtil.log(TAG, "onMeetingStatusChanged: " + minimizedLiveData.getValue());
    if (status == NEMeetingStatus.MEETING_STATUS_DISCONNECTING) {
      meetingInfoLiveData.setValue(null);
    } else if (minimizedLiveData.getValue()) {
      getMeetingService()
          .getCurrentMeetingInfo(
              (code, msg, info) -> {
                Log.i(
                    TAG,
                    "current meeting info: "
                        + info
                        + " current: "
                        + System.currentTimeMillis()
                        + " offset: "
                        + (System.currentTimeMillis() - info.startTime));
                meetingInfoLiveData.setValue(info);
                durationInitialTimestamp = SystemClock.elapsedRealtime();
              });
    }
  }

  private void calculateElapsedTime() {
    NEMeetingInfo info = meetingInfoLiveData.getValue();
    if (info != null) {
      long duration =
          (info.duration + SystemClock.elapsedRealtime() - durationInitialTimestamp) / 1000;
      long hours = duration / 3600;
      long minutes = (duration % 3600) / 60;
      long seconds = duration % 60;
      meetingTimeLiveData.setValue(String.format("%02d:%02d:%02d", hours, minutes, seconds));
      handler.sendEmptyMessageDelayed(MSG_ACTIVE, 1000);
    }
  }

  @Override
  public void onAuthStateChanged(int state) {
    stateLiveData.setValue(state);
  }

  /**
   * 订阅会议内某一音频流
   *
   * @param accountId 订阅或者取消订阅的id
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeRemoteAudioStream(
      String accountId, boolean subscribe, NECallback<Void> callback) {
    mRepository.subscribeRemoteAudioStream(accountId, subscribe, callback);
  }

  /**
   * 批量订阅会议内音频流
   *
   * @param accountIds 订阅或者取消订阅的id列表
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeRemoteAudioStreams(
      List<String> accountIds, boolean subscribe, NECallback<List<String>> callback) {
    mRepository.subscribeRemoteAudioStreams(accountIds, subscribe, callback);
  }

  /**
   * 订阅会议内全部音频流
   *
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeAllRemoteAudioStreams(boolean subscribe, NECallback<Void> callback) {
    mRepository.subscribeAllRemoteAudioStreams(subscribe, callback);
  }
}
