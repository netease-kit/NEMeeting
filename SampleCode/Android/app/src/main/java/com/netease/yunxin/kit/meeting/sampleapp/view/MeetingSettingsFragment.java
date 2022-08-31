// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.view;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

import com.netease.yunxin.kit.meeting.sampleapp.MeetingApplication;
import com.netease.yunxin.kit.meeting.sampleapp.R;
import com.netease.yunxin.kit.meeting.sampleapp.data.MeetingConfigRepository;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingVirtualBackground;
import com.netease.yunxin.kit.meeting.sdk.NESettingsService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        private final static String ENABLE_VIRTUAL_BACKGROUND = "enable_virtual_background";

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
                    case ENABLE_VIRTUAL_BACKGROUND:
                        value = settingsService.isVirtualBackgroundEnabled(); break;
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
                    case ENABLE_VIRTUAL_BACKGROUND:
                        if(value){
                            setVirtualBackgroundPic(settingsService);
                        }
                        settingsService.enableVirtualBackground(value);break;
                }
            }
        }

        /**
         * 设置虚拟背景 图片
         * @param settingsService
         */
        private void setVirtualBackgroundPic(NESettingsService settingsService) {
            List<NEMeetingVirtualBackground> virtualBackgrounds = new ArrayList<>();
            String virtualPath = MeetingApplication.getInstance().getFilesDir().getPath()+"/virtual";
            File virtualFile = new File(virtualPath);
            int size = 0;
            if(virtualFile.exists()){
               size = virtualFile.listFiles().length;
            }
            for (int i=1;i<=size;i++){
                String path = MeetingApplication.getInstance().getFilesDir().getPath()+"/virtual/"+i+".png";
                File file = new File(path);
                if(file.exists()) {
                    NEMeetingVirtualBackground virtualBackground = new NEMeetingVirtualBackground(path);
                    virtualBackgrounds.add(virtualBackground);
                }
            }
            settingsService.setBuiltinVirtualBackgrounds(virtualBackgrounds);
        }
    }
}
