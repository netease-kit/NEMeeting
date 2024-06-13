// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.data;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.netease.yunxin.kit.meeting.sdk.*;

import java.util.List;

public class MeetingDataRepository {
  private static volatile MeetingDataRepository INSTANCE;

  public static MeetingDataRepository getInstance() {
    if (INSTANCE == null) {
      synchronized (MeetingDataRepository.class) {
        if (INSTANCE == null) {
          INSTANCE = new MeetingDataRepository();
        }
      }
    }
    return INSTANCE;
  }

  public void startMeeting(
      Context context,
      @NonNull NEStartMeetingParams param,
      @Nullable NEStartMeetingOptions opts,
      NECallback<Void> callback) {
    NEMeetingKit.getInstance().getMeetingService().startMeeting(context, param, opts, callback);
  }

  public void joinMeeting(
      Context context,
      @NonNull NEJoinMeetingParams param,
      @Nullable NEJoinMeetingOptions opts,
      NECallback<Void> callback) {
    NEMeetingKit.getInstance().getMeetingService().joinMeeting(context, param, opts, callback);
  }

  public void anonymousJoinMeeting(
      Context context,
      @NonNull NEJoinMeetingParams param,
      @Nullable NEJoinMeetingOptions opts,
      NECallback<Void> callback) {
    NEMeetingKit.getInstance()
        .getMeetingService()
        .anonymousJoinMeeting(context, param, opts, callback);
  }

  public void getCurrentMeetingInfo(NECallback<NEMeetingInfo> callback) {}

  /**
   * 订阅会议内某一音频流
   *
   * @param accountId 订阅或者取消订阅的id
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeRemoteAudioStream(
      String accountId, boolean subscribe, NECallback<Void> callback) {}

  /**
   * 批量订阅会议内音频流
   *
   * @param accountIds 订阅或者取消订阅的id列表
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeRemoteAudioStreams(
      List<String> accountIds, boolean subscribe, NECallback<List<String>> callback) {}

  /**
   * 订阅会议内全部音频流
   *
   * @param subscribe true：订阅， false：取消订阅
   */
  public void subscribeAllRemoteAudioStreams(boolean subscribe, NECallback<Void> callback) {}

  public NEMeetingService getMeetingService() {
    return NEMeetingKit.getInstance().getMeetingService();
  }

  /**
   * 获取会议邀请服务
   *
   * @return 会议邀请服务
   */
  public NEMeetingInviteService getMeetingInviteService() {
    return NEMeetingKit.getInstance().getMeetingInviteService();
  }

  public void setOnInjectedMenuItemClickListener(
      NEMeetingOnInjectedMenuItemClickListener listener) {
    if (NEMeetingKit.getInstance().getMeetingService() != null) {
      NEMeetingKit.getInstance().getMeetingService().setOnInjectedMenuItemClickListener(listener);
    }
  }

  public void returnToMeeting(Context context) {}

  public NEMeetingStatus getMeetingStatus() {
    return null;
  }

  public void addMeetingStatusListener(NEMeetingStatusListener listener) {}

  public void removeMeetingStatusListener(NEMeetingStatusListener listener) {}

  /////////////////////////////////////////////////
  /** * ScheduleMeeting start * */
  /////////////////////////////////////////////////
  public NEMeetingItem createScheduleMeetingItem() {
    return NEMeetingKit.getInstance().getPreMeetingService().createScheduleMeetingItem();
  }

  public void scheduleMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
    NEMeetingKit.getInstance().getPreMeetingService().scheduleMeeting(item, callback);
  }

  public void editMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
    NEMeetingKit.getInstance().getPreMeetingService().editMeeting(item, false, callback);
  }

  public void cancelMeeting(long meetingId, NECallback<Void> callback) {
    NEMeetingKit.getInstance().getPreMeetingService().cancelMeeting(meetingId, false, callback);
  }

  public void deleteMeeting(int meetingId, NECallback<Void> callback) {}

  public void getMeetingItemById(int meetingId, NECallback<NEMeetingItem> callback) {}

  public void getMeetingList(
      List<NEMeetingItemStatus> status, NECallback<List<NEMeetingItem>> callback) {
    NEMeetingKit.getInstance().getPreMeetingService().getMeetingList(status, callback);
  }

  public void registerScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
    NEMeetingKit.getInstance()
        .getPreMeetingService()
        .registerScheduleMeetingStatusListener(listener);
  }

  public void unRegisterScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
    NEMeetingKit.getInstance()
        .getPreMeetingService()
        .unRegisterScheduleMeetingStatusListener(listener);
  }

  /////////////////////////////////////////////////
  /** * ScheduleMeeting end * */
  /////////////////////////////////////////////////

  /////////////////////////////////////////////////
  /** * SettingsService start * */
  /////////////////////////////////////////////////

  public void openBeautyUI(Context context, NECallback<Void> callback) {
    //        NEMeetingKit.getInstance().getSettingsService().openBeautyUI(context, callback);
  }

  public boolean isBeautyFaceEnabled() {
    //        return NEMeetingKit.getInstance().getSettingsService().isBeautyFaceEnabled();
    return false;
  }

  public void getBeautyFaceValue(NECallback<Integer> callback) {
    //         NEMeetingKit.getInstance().getSettingsService().getBeautyFaceValue(callback);
  }

  public void setBeautyFaceValue(int beautyFaceValue) {
    //         NEMeetingKit.getInstance().getSettingsService().setBeautyFaceValue(beautyFaceValue);
  }

  public void startScreenSharing(
      Context context,
      Intent data,
      @NonNull NEScreenSharingParams param,
      @Nullable NEScreenSharingOptions opts,
      NECallback<Void> callback) {
    NEMeetingKit.getInstance()
        .getScreenSharingService()
        .startScreenShare(context, data, param, opts, callback);
  }

  public void stopScreenShare(Context context, NECallback<Void> callback) {
    NEMeetingKit.getInstance().getScreenSharingService().stopScreenShare(callback);
  }

  public void addScreenSharingStatusListener(NEScreenSharingStatusListener listener) {
    NEMeetingKit.getInstance().getScreenSharingService().addScreenSharingStatusListener(listener);
  }

  /////////////////////////////////////////////////
  /** * SettingsService end * */
  /////////////////////////////////////////////////
}
