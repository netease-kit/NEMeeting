// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.meeting.sampleapp.log;

import android.content.Context;
import android.text.TextUtils;

/** Created by chengda.lcd on 2017/12/7. */
public final class LogUtil {

  private static Logger sGlobal;

  private LogUtil() {}

  public static synchronized void init(Context context) {
    init(context, null);
  }

  public static synchronized void init(Context context, String name) {
    if (sGlobal == null) {
      if (TextUtils.isEmpty(name)) {
        name = context.getPackageName() + ".log";
      }
      sGlobal = Logger.init(context, name);
    }
  }

  public static synchronized void quit() {
    if (sGlobal != null) {
      sGlobal.quit();
      sGlobal = null;
    }
  }

  public static void log(String tag, String message) {
    log(tag, message, null);
  }

  public static void log(String tag, String message, Throwable throwable) {
    if (sGlobal != null) {
      sGlobal.log(tag, message, throwable);
    }
  }
}
