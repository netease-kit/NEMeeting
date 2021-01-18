package com.netease.meetinglib.demo.data;


import android.content.Context;

import com.netease.meetinglib.sdk.NECallback;
import com.netease.meetinglib.sdk.NEJoinMeetingOptions;
import com.netease.meetinglib.sdk.NEJoinMeetingParams;
import com.netease.meetinglib.sdk.NEMeetingInfo;
import com.netease.meetinglib.sdk.NEMeetingItem;
import com.netease.meetinglib.sdk.NEMeetingItemStatus;
import com.netease.meetinglib.sdk.NEMeetingOnInjectedMenuItemClickListener;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingService;
import com.netease.meetinglib.sdk.NEMeetingStatus;
import com.netease.meetinglib.sdk.NEMeetingStatusListener;
import com.netease.meetinglib.sdk.NEScheduleMeetingStatusListener;
import com.netease.meetinglib.sdk.NEStartMeetingOptions;
import com.netease.meetinglib.sdk.NEStartMeetingParams;
import com.netease.meetinglib.sdk.control.NEControlListener;
import com.netease.meetinglib.sdk.control.NEControlMenuItemClickListener;
import com.netease.meetinglib.sdk.control.NEControlOptions;
import com.netease.meetinglib.sdk.control.NEControlParams;

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
        NEMeetingSDK.getInstance().getMeetingService().startMeeting(context, param, opts, callback);
    }

    public void joinMeeting(Context context, @NonNull NEJoinMeetingParams param, @Nullable NEJoinMeetingOptions opts, NECallback<Void> callback) {
        NEMeetingSDK.getInstance().getMeetingService().joinMeeting(context, param, opts, callback);
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
        NEMeetingSDK.getInstance().getMeetingService().subscribeRemoteAudioStream(accountId, subscribe, callback);
    }

    /**
     * 批量订阅会议内音频流
     *
     * @param accountIds 订阅或者取消订阅的id列表
     * @param subscribe  true：订阅， false：取消订阅
     */
    public void subscribeRemoteAudioStreams(List<String> accountIds, boolean subscribe, NECallback<List<String>> callback) {
        NEMeetingSDK.getInstance().getMeetingService().subscribeRemoteAudioStreams(accountIds, subscribe, callback);
    }

    /**
     * 订阅会议内全部音频流
     *
     * @param subscribe true：订阅， false：取消订阅
     */
    public void subscribeAllRemoteAudioStreams(boolean subscribe, NECallback<Void> callback) {
        NEMeetingSDK.getInstance().getMeetingService().subscribeAllRemoteAudioStreams(subscribe, callback);
    }

    public NEMeetingService getMeetingService() {
        return NEMeetingSDK.getInstance().getMeetingService();
    }

    public void setOnInjectedMenuItemClickListener(NEMeetingOnInjectedMenuItemClickListener listener) {
        if (NEMeetingSDK.getInstance().getMeetingService() != null) {
            NEMeetingSDK.getInstance().getMeetingService().setOnInjectedMenuItemClickListener(listener);
        }
    }

    public void setOnControllerInjectedMenuItemClickListener(NEMeetingOnInjectedMenuItemClickListener listener) {
        if (NEMeetingSDK.getInstance().getMeetingService() != null) {
            NEMeetingSDK.getInstance().getControlService().setOnInjectedMenuItemClickListener(listener);
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
        return NEMeetingSDK.getInstance().getPreMeetingService().createScheduleMeetingItem();
    }

    public void scheduleMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {
        NEMeetingSDK.getInstance().getPreMeetingService().scheduleMeeting(item,callback);
    }

    public void editMeeting(NEMeetingItem item, NECallback<NEMeetingItem> callback) {

    }

    public void cancelMeeting(long meetingUniqueId, NECallback<Void> callback) {
        NEMeetingSDK.getInstance().getPreMeetingService().cancelMeeting(meetingUniqueId,callback);
    }

    public void deleteMeeting(int meetingUniqueId, NECallback<Void> callback) {

    }

    public void getMeetingItemById(int meetingUniqueId, NECallback<NEMeetingItem> callback) {

    }

    public void getMeetingList(List<NEMeetingItemStatus> status, NECallback<List<NEMeetingItem>> callback) {
        NEMeetingSDK.getInstance().getPreMeetingService().getMeetingList(status,callback);
    }

    public void registerScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
        NEMeetingSDK.getInstance().getPreMeetingService().registerScheduleMeetingStatusListener(listener);
    }

    public void unRegisterScheduleMeetingStatusListener(NEScheduleMeetingStatusListener listener) {
        NEMeetingSDK.getInstance().getPreMeetingService().unRegisterScheduleMeetingStatusListener(listener);
    }

    /////////////////////////////////////////////////
    /***          ScheduleMeeting end           **/
    /////////////////////////////////////////////////


    /////////////////////////////////////////////////
    /***          ControlService start           **/
    /////////////////////////////////////////////////

    public void openController(Context context, NEControlParams params, NEControlOptions opts, NECallback<Void> callback) {
        NEMeetingSDK.getInstance().getControlService().openControlUI(context, params, opts, callback);
    }

    public void setOnControlCustomMenuItemClickListener(NEControlMenuItemClickListener listener) {
        NEMeetingSDK.getInstance().getControlService().setOnCustomMenuItemClickListener(listener);
    }

    public void registerControlListener(NEControlListener listener) {
        NEMeetingSDK.getInstance().getControlService().registerControlListener(listener);
    }

    public void unRegisterControlListener(NEControlListener listener) {
        NEMeetingSDK.getInstance().getControlService().unRegisterControlListener(listener);
    }

    /////////////////////////////////////////////////
    /***          ControlService end           **/
    /////////////////////////////////////////////////



    /////////////////////////////////////////////////
    /***          SettingsService start           **/
    /////////////////////////////////////////////////

    public void openBeautyUI(Context context,  NECallback<Void> callback) {
        NEMeetingSDK.getInstance().getSettingsService().openBeautyUI(context, callback);
    }

    public boolean isBeautyFaceEnabled() {
        return NEMeetingSDK.getInstance().getSettingsService().isBeautyFaceEnabled();
    }

    public void getBeautyFaceValue(NECallback<Integer> callback) {
        NEMeetingSDK.getInstance().getSettingsService().getBeautyFaceValue(callback);
    }

    public void setBeautyFaceValue(int beautyFaceValue) {
        NEMeetingSDK.getInstance().getSettingsService().setBeautyFaceValue(beautyFaceValue);
    }

    /////////////////////////////////////////////////
    /***          SettingsService end           **/
    /////////////////////////////////////////////////
}
