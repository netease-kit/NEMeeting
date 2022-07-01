// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.data;


import android.content.Context;

import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEJoinMeetingParams;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingInfo;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItem;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingItemStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingService;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatus;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingStatusListener;
import com.netease.yunxin.kit.meeting.sdk.NEScheduleMeetingStatusListener;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingOptions;
import com.netease.yunxin.kit.meeting.sdk.NEStartMeetingParams;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeetingDataRepository {
    private volatile static MeetingDataRepository INSTANCE;

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

    public void startMeeting(Context context, @NonNull NEStartMeetingParams param, @Nullable NEStartMeetingOptions opts, NECallback<Void> callback) {
        NEMeetingKit.getInstance().getMeetingService().startMeeting(context, param, opts, callback);
    }

    public void joinMeeting(Context context, @NonNull NEJoinMeetingParams param, @Nullable NEJoinMeetingOptions opts, NECallback<Void> callback) {
        NEMeetingKit.getInstance().getMeetingService().joinMeeting(context, param, opts, callback);
    }

    public void anonymousJoinMeeting(Context context, @NonNull NEJoinMeetingParams param, @Nullable NEJoinMeetingOptions opts, NECallback<Void> callback) {
        NEMeetingKit.getInstance().getMeetingService().anonymousJoinMeeting(context, param, opts, callback);
    }

    public void getCurrentMeetingInfo(NECallback<NEMeetingInfo> callback) {
    }

    /**
     * 订阅会议内某一音频流
     *
     * @param accountId 订阅或者取消订阅的id
     * @param subscribe true：订阅， false：取消订阅
     */
    public void subscribeRemoteAudioStream(String accountId, boolean subscribe, NECallback<Void> callback) {
    }

    /**
     * 批量订阅会议内音频流
     *
     * @param accountIds 订阅或者取消订阅的id列表
     * @param subscribe  true：订阅， false：取消订阅
     */
    public void subscribeRemoteAudioStreams(List<String> accountIds, boolean subscribe, NECallback<List<String>> callback) {
    }

    /**
     * 订阅会议内全部音频流
     *
     * @param subscribe true：订阅， false：取消订阅
     */
    public void subscribeAllRemoteAudioStreams(boolean subscribe, NECallback<Void> callback) {
    }

    public NEMeetingService getMeetingService() {
        return NEMeetingKit.getInstance().getMeetingService();
    }

    public void setOnInjectedMenuItemClickListener(NEMeetingOnInjectedMenuItemClickListener listener) {
        if (NEMeetingKit.getInstance().getMeetingService() != null) {
            NEMeetingKit.getInstance().getMeetingService().setOnInjectedMenuItemClickListener(listener);
        }
    }

    public void returnToMeeting(Context context) {

    }

    public NEMeetingStatus getMeetingStatus() {
        return null;
    }

    public void addMeetingStatusListener(NEMeetingStatusListener listener) {

    }

    public void removeMeetingStatusListener(NEMeetingStatusListener listener) {

    }

    /////////////////////////////////////////////////
    /***          ScheduleMeeting start           **/
    /////////////////////////////////////////////////
    public NEMeetingItem createScheduleMeetingItem() {
        return NEMeetingKit.getInstance().getPreMeetingService().createScheduleMeetingItem();
    }

    public void scheduleMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
        NEMeetingKit.getInstance().getPreMeetingService().scheduleMeeting(item,callback);
    }

    public void editMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
        NEMeetingKit.getInstance().getPreMeetingService().editMeeting(item,callback);
    }

    public void cancelMeeting(long meetingUniqueId, NECallback<Void> callback) {
        NEMeetingKit.getInstance().getPreMeetingService().cancelMeeting(meetingUniqueId,callback);
    }

    public void deleteMeeting(int meetingUniqueId, NECallback<Void> callback) {

    }

    public void getMeetingItemById(int meetingUniqueId, NECallback<NEMeetingItem> callback) {

    }

    public void getMeetingList(List<NEMeetingItemStatus> status, NECallback<List<NEMeetingItem>> callback) {
        NEMeetingKit.getInstance().getPreMeetingService().getMeetingList(status,callback);
    }

    public void registerScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
        NEMeetingKit.getInstance().getPreMeetingService().registerScheduleMeetingStatusListener(listener);
    }

    public void unRegisterScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
        NEMeetingKit.getInstance().getPreMeetingService().unRegisterScheduleMeetingStatusListener(listener);
    }

    /////////////////////////////////////////////////
    /***          ScheduleMeeting end           **/
    /////////////////////////////////////////////////


    /////////////////////////////////////////////////
    /***          SettingsService start           **/
    /////////////////////////////////////////////////

    public void openBeautyUI(Context context,  NECallback<Void> callback) {
//        NEMeetingKit.getInstance().getSettingsService().openBeautyUI(context, callback);
    }

    public boolean isBeautyFaceEnabled() {
//        return NEMeetingKit.getInstance().getSettingsService().isBeautyFaceEnabled();
        return  false;
    }

    public void getBeautyFaceValue(NECallback<Integer> callback) {
//         NEMeetingKit.getInstance().getSettingsService().getBeautyFaceValue(callback);
    }

    public void setBeautyFaceValue(int beautyFaceValue) {
//         NEMeetingKit.getInstance().getSettingsService().setBeautyFaceValue(beautyFaceValue);
    }

    /////////////////////////////////////////////////
    /***          SettingsService end           **/
    /////////////////////////////////////////////////
}
