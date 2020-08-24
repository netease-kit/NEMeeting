/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

import com.netease.meetinglib.demo.R;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NESettingsService;

public class MeetingSettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "MeetingSettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(new DataStore());
        setPreferencesFromResource(R.xml.meeting_settings, rootKey);
    }

    private static class DataStore extends PreferenceDataStore {

        private final static String ENABLE_SHOW_MEETING_TIME = "enable_show_meeting_time";
        private final static String ENABLE_VIDEO = "enable_video";
        private final static String ENABLE_AUDIO = "enable_audio";

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            boolean value = defValue;
            NESettingsService settingsService = NEMeetingSDK.getInstance().getSettingsService();
            if (settingsService != null) {
                switch (key) {
                    case ENABLE_SHOW_MEETING_TIME:
                        value = settingsService.isShowMyMeetingElapseTimeEnabled();break;
                    case ENABLE_VIDEO:
                        value = settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();break;
                    case ENABLE_AUDIO:
                        value = settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();break;
                }
            }
            Log.i(TAG, "getBoolean: " + key + '=' + value);
            return value;
        }

        @Override
        public void putBoolean(String key, boolean value) {
            Log.i(TAG, "putBoolean: " + key + '=' + value);
            NESettingsService settingsService = NEMeetingSDK.getInstance().getSettingsService();
            if (settingsService != null) {
                switch (key) {
                    case ENABLE_SHOW_MEETING_TIME:
                        settingsService.enableShowMyMeetingElapseTime(value);break;
                    case ENABLE_VIDEO:
                        settingsService.setTurnOnMyVideoWhenJoinMeeting(value);break;
                    case ENABLE_AUDIO:
                        settingsService.setTurnOnMyAudioWhenJoinMeeting(value);break;
                }
            }
        }
    }
}
