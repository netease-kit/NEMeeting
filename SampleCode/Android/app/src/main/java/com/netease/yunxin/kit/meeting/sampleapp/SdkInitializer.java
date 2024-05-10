// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;
import androidx.annotation.NonNull;
import com.netease.yunxin.kit.meeting.sampleapp.data.ServerConfig;
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKit;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingKitConfig;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingLanguage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SdkInitializer {

  private static final String TAG = "SdkInitializer";

  private static class Holder {
    private static SdkInitializer INSTANCE = new SdkInitializer();
  }

  public static SdkInitializer getInstance() {
    return Holder.INSTANCE;
  }

  private SdkInitializer() {}

  private Context context;
  private boolean started = false;
  private boolean initialized = false;
  private int initializeIndex = 0;
  private ConnectivityManager.NetworkCallback networkCallback;
  private Set<InitializeListener> listenerSet;

  public void startInitialize(Context context) {
    if (!started) {
      started = true;
      this.context = context;
      initializeSdk();
    }
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void addListener(InitializeListener listener) {
    if (listenerSet == null) {
      listenerSet = new HashSet<>();
    }
    if (isInitialized()) {
      listener.onInitialized(initializeIndex);
    } else {
      listenerSet.add(listener);
    }
  }

  public void removeListener(InitializeListener listener) {
    if (listenerSet != null && listener != null) {
      listenerSet.remove(listener);
    }
  }

  public int getLoggerLevelConfig() {
    int level = 0;
    try {
      level = Integer.parseInt(SPUtils.getInstance().getString("meeting-logger-level-config"));
    } catch (NumberFormatException ignored) {
    }
    return level;
  }

  public String getLoggerPathConfig() {
    return SPUtils.getInstance().getString("meeting-logger-path-config");
  }

  private void listenLanguageSettingChange() {
    SPUtils.getInstance()
        .registerOnSharedPreferenceChangeListener(
            (sharedPreferences, key) -> {
              if (key.equals("meeting-language")) {
                NEMeetingKit.getInstance()
                    .switchLanguage(
                        getSelectLanguage(), new ToastCallback<>(context, "switch language"));
              }
            });
  }

  private NEMeetingLanguage getSelectLanguage() {
    SPUtils sp = SPUtils.getInstance();
    String languageCode = sp.getString("meeting-language");
    for (NEMeetingLanguage value : NEMeetingLanguage.values()) {
      if (value.languageCode.equals(languageCode)) {
        return value;
      }
    }
    return NEMeetingLanguage.AUTOMATIC;
  }

  private void initializeSdk() {
    listenLanguageSettingChange();

    Log.i(TAG, "initializeSdk");
    ServerConfig serverConfig = MeetingApplication.getInstance().getServerConfig();
    NEMeetingKitConfig config = new NEMeetingKitConfig();
    config.appKey = serverConfig.getAppKey();
    config.language = getSelectLanguage();
    /// to remove start
    config.extras =
        new HashMap<String, Object>() {
          {
            put("debugMode", serverConfig.isDebugMode() ? 1 : 0);
          }
        };
    config.appName = context.getString(R.string.app_name);
    config.serverUrl = serverConfig.getServerUrl();
    config.useAssetServerConfig = serverConfig.getUseAssetServerConfig();
    NEMeetingKit.getInstance()
        .initialize(
            context,
            config,
            new ToastCallback<Void>(context, "初始化") {
              @Override
              public void onResult(int resultCode, String resultMsg, Void resultData) {
                super.onResult(resultCode, resultMsg, resultData);
                if (resultCode == NEMeetingError.ERROR_CODE_SUCCESS) {
                  notifyInitialized();
                  unregisterNetworkCallback();
                } else {
                  reInitializeWhenNetworkAvailable();
                }
              }
            });
    initializeIndex++;
  }

  private void notifyInitialized() {
    initialized = true;
    int times = initializeIndex;
    for (InitializeListener listener : listenerSet) {
      listener.onInitialized(times);
    }
    SimpleMeetingEncryptor.getInstance().init();
  }

  private void reInitializeWhenNetworkAvailable() {
    if (networkCallback == null) {
      ConnectivityManager connectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkRequest networkRequest =
          new NetworkRequest.Builder()
              .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
              .build();
      networkCallback =
          new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
              Log.i(TAG, "on network available");
              initializeSdk();
            }
          };
      connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }
  }

  private void unregisterNetworkCallback() {
    if (networkCallback != null) {
      ConnectivityManager connectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      connectivityManager.unregisterNetworkCallback(networkCallback);
    }
  }

  public interface InitializeListener {

    void onInitialized(int initializeIndex);
  }
}
