/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.netease.meetinglib.sdk.NEMeetingError;
import com.netease.meetinglib.sdk.NEMeetingSDK;
import com.netease.meetinglib.sdk.NEMeetingSDKConfig;

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
    private int initializeTimes = 0;
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
        listenerSet.add(listener);
    }

    public void removeListener(InitializeListener listener) {
        if (listenerSet != null && listener != null) {
            listenerSet.remove(listener);
        }
    }

    private void initializeSdk() {
        Log.i(TAG, "initializeSdk");
        NEMeetingSDKConfig config = new NEMeetingSDKConfig();
        config.appKey = context.getString(R.string.appkey);
        config.appName = context.getString(R.string.app_name);
        NEMeetingSDK.getInstance().initialize(context, config, new ToastCallback<Void>(context,"初始化"){
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
        initializeTimes++;
    }

    private void notifyInitialized() {
        initialized = true;
        int times = initializeTimes;
        for (InitializeListener listener : listenerSet) {
            listener.onInitialized(times);
        }
    }

    private void reInitializeWhenNetworkAvailable() {
        if (networkCallback == null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest networkRequest =
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build();
            networkCallback = new ConnectivityManager.NetworkCallback() {
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

        void onInitialized(int total);

    }
}
