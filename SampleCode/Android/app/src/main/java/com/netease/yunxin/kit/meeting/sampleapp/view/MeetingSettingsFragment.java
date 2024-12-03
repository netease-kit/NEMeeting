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
import com.netease.yunxin.kit.meeting.sdk.NEMeetingElapsedTimeDisplayType;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
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

    private static final String MEETING_ELAPSED_TIME = "meeting_elapsed_time";
    private static final String ENABLE_VIDEO = "enable_video";
    private static final String ENABLE_AUDIO = "enable_audio";
    private static final String ENABLE_AUDIO_AINS = "enable_audio_ains";
    private static final String ENABLE_AUDIO_OPTIONS = "enable_audio_options";
    private static final String JOIN_TIMEOUT = "join_timeout_millis";
    private static final String AUDIO_PROFILE = "audioProfile";
    private static final String AUDIO_SCENARIO = "audioScenario";
    private static final String ENABLE_VIRTUAL_BACKGROUND = "enable_virtual_background";
    private static final String ENABLE_SPEAKER_SPOTLIGHT = "enable_speaker_spotlight";
    private static final String ENABLE_FRONT_CAMERA_MIRROR = "enable_front_camera_mirror";
    private static final String ENABLE_TRANSPARENT_WHITEBOARD = "enable_transparent_whiteboard";

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
      switch (key) {
        case JOIN_TIMEOUT:
          return String.valueOf(MeetingConfigRepository.INSTANCE.getJoinTimeout());
        case AUDIO_PROFILE:
          return MeetingConfigRepository.INSTANCE.getAudioProfile();
        case AUDIO_SCENARIO:
          return MeetingConfigRepository.INSTANCE.getAudioScenario();
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
        case AUDIO_SCENARIO:
          MeetingConfigRepository.INSTANCE.setAudioScenario(value);
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
          case MEETING_ELAPSED_TIME:
            value =
                settingsService.getMeetingElapsedTimeDisplayType()
                    == NEMeetingElapsedTimeDisplayType.PARTICIPATION_ELAPSED_TIME;
            break;
          case ENABLE_VIDEO:
            value = settingsService.isTurnOnMyVideoWhenJoinMeetingEnabled();
            break;
          case ENABLE_AUDIO:
            value = settingsService.isTurnOnMyAudioWhenJoinMeetingEnabled();
            break;
          case ENABLE_AUDIO_AINS:
            value = settingsService.isAudioAINSEnabled();
            break;
          case ENABLE_VIRTUAL_BACKGROUND:
            value = settingsService.isVirtualBackgroundEnabled();
            break;
          case ENABLE_SPEAKER_SPOTLIGHT:
            value = settingsService.isSpeakerSpotlightEnabled();
            break;
          case ENABLE_TRANSPARENT_WHITEBOARD:
            value = settingsService.isTransparentWhiteboardEnabled();
            break;
          case ENABLE_FRONT_CAMERA_MIRROR:
            value = settingsService.isFrontCameraMirrorEnabled();
            break;
          case ENABLE_AUDIO_OPTIONS:
            return MeetingConfigRepository.INSTANCE.getEnableAudioOptions();
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
          case MEETING_ELAPSED_TIME:
            settingsService.setMeetingElapsedTimeDisplayType(
                value
                    ? NEMeetingElapsedTimeDisplayType.PARTICIPATION_ELAPSED_TIME
                    : NEMeetingElapsedTimeDisplayType.MEETING_ELAPSED_TIME);
            break;
          case ENABLE_VIDEO:
            settingsService.enableTurnOnMyVideoWhenJoinMeeting(value);
            break;
          case ENABLE_AUDIO:
            settingsService.enableTurnOnMyAudioWhenJoinMeeting(value);
            break;
          case ENABLE_AUDIO_AINS:
            settingsService.enableAudioAINS(value);
            break;
          case ENABLE_VIRTUAL_BACKGROUND:
            if (value) {
              setVirtualBackgroundPic(settingsService);
            } else {
              settingsService.setBuiltinVirtualBackgroundList(null);
            }
            settingsService.enableVirtualBackground(value);
            break;
          case ENABLE_AUDIO_OPTIONS:
            MeetingConfigRepository.INSTANCE.setEnableAudioOptions(value);
            break;
          case ENABLE_SPEAKER_SPOTLIGHT:
            settingsService.enableSpeakerSpotlight(value);
            break;
          case ENABLE_TRANSPARENT_WHITEBOARD:
            settingsService.enableTransparentWhiteboard(value);
            break;
          case ENABLE_FRONT_CAMERA_MIRROR:
            settingsService.enableFrontCameraMirror(value);
            break;
        }
      }
    }

    /** 设置虚拟背景 图片 */
    private void setVirtualBackgroundPic(NESettingsService settingsService) {
      List<String> virtualBackgrounds = new ArrayList<>();
      String virtualPath = MeetingApplication.getInstance().getFilesDir().getPath() + "/virtual";
      File virtualFile = new File(virtualPath);
      int size = 0;
      if (virtualFile.exists()) {
        size = virtualFile.listFiles().length;
      }
      for (int i = 1; i <= size; i++) {
        String path =
            MeetingApplication.getInstance().getFilesDir().getPath() + "/virtual/" + i + ".png";
        File file = new File(path);
        if (file.exists()) {
          virtualBackgrounds.add(path);
        }
      }
      settingsService.setBuiltinVirtualBackgroundList(virtualBackgrounds);
    }
  }
}
