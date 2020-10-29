package com.netease.meetinglib.demo.log;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by chengda.lcd on 2017/12/7.
 */

public final class LogUtil {

    private static Logger sGlobal;

    private LogUtil(){}

    public synchronized static void init(Context context){
        init(context, null);
    }

    public synchronized static void init(Context context, String name){
        if (sGlobal == null) {
            if (TextUtils.isEmpty(name)){
                name = context.getPackageName() + ".log";
            }
            sGlobal = Logger.init(context, name);
        }
    }

    public synchronized static void quit(){
        if (sGlobal != null){
            sGlobal.quit();
            sGlobal = null;
        }
    }

    public static void log(String tag, String message){
        log(tag, message, null);
    }

    public static void log(String tag, String message, Throwable throwable){
        if (sGlobal != null){
            sGlobal.log(tag, message, throwable);
        }
    }
}
