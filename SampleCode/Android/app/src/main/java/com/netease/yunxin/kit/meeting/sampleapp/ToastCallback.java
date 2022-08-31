// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp;

import android.content.Context;
import android.widget.Toast;

import com.netease.yunxin.kit.meeting.sampleapp.log.LogUtil;
import com.netease.yunxin.kit.meeting.sdk.NECallback;
import com.netease.yunxin.kit.meeting.sdk.NEMeetingError;

public class ToastCallback<T> implements NECallback<T> {

    private static final String TAG = "ApiResult";

    public final Context context;
    public final String prefix;

    public ToastCallback(Context context, String prefix) {
        this.context = context.getApplicationContext();
        this.prefix = prefix;
    }

    @Override
    public void onResult(int resultCode, String resultMsg, T resultData) {
        LogUtil.log(TAG, prefix + " onResult: " + resultCode + '|' + resultMsg + '|' + resultData);
        Toast.makeText(context, prefix + (resultCode == NEMeetingError.ERROR_CODE_SUCCESS ? "成功" : "失败" + (resultMsg != null ? ": " + resultMsg : "")),
                Toast.LENGTH_SHORT).show();
    }
}