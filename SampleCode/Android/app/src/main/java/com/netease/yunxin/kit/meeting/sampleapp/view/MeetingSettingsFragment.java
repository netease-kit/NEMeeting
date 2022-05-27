/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingConfigRepository;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;

public class MeetingSettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "MeetingSettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(new DataStore());
        setPreferencesFromResource(R.xml.meeting_settings, rootKey);
        ((EditTextPreference) findPreference(DataStore.JOIN_TIMEOUT))
                .setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
    }

    private static class DataStore extends PreferenceDataStore {

        private final static String ENABLE_SHOW_MEETING_TIME = "enable_show_meeting_time";
        private final static String ENABLE_VIDEO = "enable_video";
        private final static String ENABLE_AUDIO = "enable_audio";
        private final static String ENABLE_AUDIO_AINS = "enable_audio_ains";
        private final static String JOIN_TIMEOUT = "join_timeout_millis";
        private final static String AUDIO_PROFILE = "audioProfile";

        @Nullable
        @Override
        public String getString(String key, @Nullable String defValue) {
            switch (key) {
                case JOIN_TIMEOUT:
                    return String.valueOf(MeetingConfigRepository.INSTANCE.getJoinTimeout());
                case AUDIO_PROFILE:
                    return MeetingConfigRepository.INSTANCE.getAudioProfile();
            }
            return super.getString(key, defValue);
        }

        @Override
        public void putString(String key, @Nullable String value) {
            switch (key) {
                case JOIN_TIMEOUT:
                    try {
                        MeetingConfigRepository.INSTANCE.setJoinTimeout(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                case AUDIO_PROFILE:
                    MeetingConfigRepository.INSTANCE.setAudioProfile(value);
                    break;
                default:
                    super.putString(key, value);
            }
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            boolean value = defValue;
            NESettingsService settingsService = NEMeetingKit.getInstance().getSettingsService();
            if (settingsService != null) {
                switch (key) {
                    case ENABLE_SHOW_MEETING_TIME:
                        value = settingsService.isShowMyMeetingElapseTimeEnabled();break;
                    case ENABLE_VIDEO:
                        value = settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();break;
                    case ENABLE_AUDIO:
                        value = settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();break;
                    case ENABLE_AUDIO_AINS:
                        value = settingsService.isAudioAINSEnabled();break;
                }
            }
            Log.i(TAG, "getBoolean: " + key + '=' + value);
            return value;
        }

        @Override
        public void putBoolean(String key, boolean value) {
            Log.i(TAG, "putBoolean: " + key + '=' + value);
            NESettingsService settingsService = NEMeetingKit.getInstance().getSettingsService();
            if (settingsService != null) {
                switch (key) {
                    case ENABLE_SHOW_MEETING_TIME:
                        settingsService.enableShowMyMeetingElapseTime(value);break;
                    case ENABLE_VIDEO:
                        settingsService.setTurnOnMyVideoWhenJoinMeeting(value);break;
                    case ENABLE_AUDIO:
                        settingsService.setTurnOnMyAudioWhenJoinMeeting(value);break;
                    case ENABLE_AUDIO_AINS:
                        settingsService.enableAudioAINS(value);break;
                }
            }
        }
    }
}
