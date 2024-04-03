// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.content.SharedPreferences;
import android.util.Log;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.encryption.NERtcEncryptionConfig;
import com.netease.lava.nertc.sdk.encryption.NERtcPacket;
import com.netease.lava.nertc.sdk.encryption.NERtcPacketObserver;
import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils;
import com.netease.yunxin.kit.meeting.sdk.NEGlobalEventListener;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NERtcWrapper;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleMeetingEncryptor extends NEGlobalEventListener
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = "SimpleMeetingEncryptor";
  private static final String KEY_ENABLE_AUDIO_ENCRYPT = "enable-meeting-audio-encrypt";
  private static final String KEY_ENABLE_VIDEO_ENCRYPT = "enable-meeting-video-encrypt";

  private static final SimpleMeetingEncryptor instance = new SimpleMeetingEncryptor();

  public static SimpleMeetingEncryptor getInstance() {
    return instance;
  }

  private boolean enableAudioEncryption;
  private boolean enableVideoEncryption;

  private SimpleMeetingEncryptor() {
    SPUtils sp = SPUtils.getInstance();
    enableAudioEncryption = sp.getBoolean(KEY_ENABLE_AUDIO_ENCRYPT, false);
    enableVideoEncryption = sp.getBoolean(KEY_ENABLE_VIDEO_ENCRYPT, false);
    sp.registerOnSharedPreferenceChangeListener(this);
    LogUtil.log(
        TAG,
        "enableAudioEncryption="
            + enableAudioEncryption
            + ", enableVideoEncryption="
            + enableVideoEncryption);
  }

  private final AtomicBoolean isInit = new AtomicBoolean(false);

  void init() {
    if (isInit.compareAndSet(false, true)) {
      NEMeetingKit.getInstance().addGlobalEventListener(this);
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(KEY_ENABLE_AUDIO_ENCRYPT)) {
      enableAudioEncryption = sharedPreferences.getBoolean(key, false);
    } else if (key.equals(KEY_ENABLE_VIDEO_ENCRYPT)) {
      enableVideoEncryption = sharedPreferences.getBoolean(key, false);
    }
    LogUtil.log(
        TAG,
        "enableAudioEncryption="
            + enableAudioEncryption
            + ", enableVideoEncryption="
            + enableVideoEncryption);
  }

  @Override
  public void beforeRtcEngineInitialize(String meetingNum, NERtcWrapper rtcWrapper) {
    Log.d(TAG, "beforeRtcEngineInitialize: meetingNum=" + meetingNum);
  }

  @Override
  public void afterRtcEngineInitialize(String meetingNum, NERtcWrapper rtcWrapper) {
    Log.d(TAG, "afterRtcEngineInitialize: meetingNum=" + meetingNum);
    if (enableAudioEncryption || enableVideoEncryption) {
      NERtcEx rtcEngine = rtcWrapper.getRtcEngine();
      int result =
          rtcEngine.enableEncryption(
              true,
              new NERtcEncryptionConfig(
                  NERtcEncryptionConfig.EncryptionMode.EncryptionModeCustom,
                  /// mode选择内置加密时有效，选择自定义加密时无效
                  null,
                  /// 自定义加密回调 observer, mode 为自定义加密时需要设置
                  new PacketObserver()));
      LogUtil.log(TAG, "registerPacketObserver result: " + result);
    }
  }

  @Override
  public void beforeRtcEngineRelease(String meetingNum, NERtcWrapper rtcWrapper) {
    Log.d(TAG, "beforeRtcEngineRelease: meetingNum=" + meetingNum);
    int result = rtcWrapper.getRtcEngine().enableEncryption(false, null);
    LogUtil.log(TAG, "unregisterPacketObserver result: " + result);
  }

  private class PacketObserver implements NERtcPacketObserver {

    @Override
    public boolean onSendAudioPacket(NERtcPacket neRtcPacket) {
      if (enableAudioEncryption) {
        ByteBuffer buffer = neRtcPacket.getData();
        neRtcPacket.setData(encrypt(buffer, neRtcPacket.getSize()));
      }
      return true;
    }

    @Override
    public boolean onSendVideoPacket(NERtcPacket neRtcPacket) {
      if (enableVideoEncryption) {
        ByteBuffer buffer = neRtcPacket.getData();
        neRtcPacket.setData(encrypt(buffer, neRtcPacket.getSize()));
      }
      return true;
    }

    @Override
    public boolean onReceiveAudioPacket(NERtcPacket neRtcPacket) {
      if (enableAudioEncryption) {
        ByteBuffer buffer = neRtcPacket.getData();
        neRtcPacket.setData(decrypt(buffer, neRtcPacket.getSize()));
      }
      return true;
    }

    @Override
    public boolean onReceiveVideoPacket(NERtcPacket neRtcPacket) {
      if (enableVideoEncryption) {
        ByteBuffer buffer = neRtcPacket.getData();
        neRtcPacket.setData(decrypt(buffer, neRtcPacket.getSize()));
      }
      return true;
    }

    private ByteBuffer encrypt(ByteBuffer buffer, int size) {
      for (int i = 0; i < size; i++) {
        byte b = buffer.get(i);
        buffer.put(i, (byte) ~(b & 0xFF));
      }
      return buffer;
    }

    private ByteBuffer decrypt(ByteBuffer buffer, int size) {
      for (int i = 0; i < size; i++) {
        byte b = buffer.get(i);
        buffer.put(i, (byte) ~(b & 0xFF));
      }
      return buffer;
    }
  };
}
