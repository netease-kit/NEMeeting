package com.netease.yunxin.kit.meeting.sampleapp.log;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class Logger {
    private static final String TAG = "Logger";

    private static final Map<String,Logger> CACHES = new HashMap<>(4);

    private final String key;
    private int references = 1;

    Logger(String key){
        this.key = key;
    }

    protected void incReference(){
        references++;
    }

    public void quit(){
        if (--references == 0){
            CACHES.remove(key);
            onQuit();
        }
    }

    public abstract void log(String tag, String message);

    public abstract void log(String tag, String message, Throwable throwable);

    /**
     * release resources here
     */
    protected void onQuit(){}


    ////////////////////

    /**
     *
     * @param context Application context
     * @param fileName log file name, located at /sdcard/Android/data/{packagename}/filels/log/{file}
     * @return a logger instance
     */
    public static Logger init(Context context, String fileName){
        return init(context, fileName, null);
    }

    /**
     *
     * @param context Application context
     * @param fileName log file name, located at /sdcard/Android/data/{packagename}/filels/log/{file}
     * @param handler if not null, use it for writing log file; Else start a new background thread for writing
     * @return a logger instance
     */
    public static Logger init(Context context, String fileName, Handler handler){
        if (context == null || TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("Init logger error -> invalid parameters");
        }
        if (CACHES.containsKey(fileName)){
            Logger cache = CACHES.get(fileName);
            cache.incReference();
            return CACHES.get(fileName);
        }
        Logger instance = null;
        if (context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Logger init failed: permission 'android.permission.WRITE_EXTERNAL_STORAGE' is required!");
            instance = new IDLE(fileName);
        }else {
            File path = context.getExternalFilesDir("log");
            if (path == null) {
                Log.w(TAG, "Logger init failed: null external file directory!");
                instance = new IDLE(fileName);
            }else {
                instance = new LoggerImp(context, path.getAbsolutePath() + "/" + fileName, handler);
            }
        }
        CACHES.put(fileName, instance);
        return instance;
    }

    private static class IDLE extends Logger{

        IDLE(String key) {
            super(key);
        }

        @Override
        public void log(String tag, String message) {
            //no op
        }

        @Override
        public void log(String tag, String message, Throwable throwable) {
            //no op
        }
    }


    private static class LoggerImp extends Logger{
        private static final int MSG_LOG = 1;
        private static final int MSG_QUIT = 2;

        private static final String SPACE = " ";
        SimpleDateFormat mDateFormat;
        Date mDate;

        static int sIndex = 0;

        private String mFile;
        private boolean mDebug;
        private boolean mInit;
        private HandlerThread mWorker;
        private Handler mHandler;
        private BufferedWriter mLoggerWriter;
        private final boolean mUseExternalHandler;

        LoggerImp(Context context, String file, Handler handler) {
            super(file);
            mFile = file;
            mDebug = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
            if (handler != null){
                mUseExternalHandler = true;
                mHandler = handler;
                if (handler.getLooper() == Looper.getMainLooper()){
                    Log.w(TAG, "Init logger use main looper will lead writing on main thread, is that you want?");
                }
            }else {
                mUseExternalHandler = false;
            }
        }

        private synchronized void init(){
            if (!mInit){
                mInit = true;
                boolean goesWell = true;
                //ensure log file
                File logFile = new File(mFile);
                if (logFile.isDirectory()){
                    goesWell = logFile.delete();
                }else if(!logFile.exists()){
                    File parent = logFile.getParentFile();
                    if (parent.isDirectory() && !parent.exists()) {
                        goesWell = parent.mkdirs();
                    }
                }

                if (!goesWell) return;

                //ensure worker
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.what == MSG_LOG) {
                            String message = (String) msg.obj;
                            if (mLoggerWriter == null) {
                                try {
                                    mLoggerWriter = new BufferedWriter(new OutputStreamWriter(new
                                            FileOutputStream(mFile, true)));
                                    mLoggerWriter.newLine();
                                    mLoggerWriter.write("-----------beginning");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (mLoggerWriter != null) {
                                try {
                                    mLoggerWriter.newLine();
                                    mLoggerWriter.write(message);
                                    mLoggerWriter.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        } else if (msg.what == MSG_QUIT) {
                            if (mLoggerWriter != null) {
                                try {
                                    mLoggerWriter.flush();
                                    mLoggerWriter.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            mWorker.quit();
                            mWorker = null;
                            return true;
                        }
                        return false;
                    }
                };
                if (!mUseExternalHandler) {
                    mWorker = new HandlerThread("logger-" + ++sIndex);
                    mWorker.start();
                    mHandler = new Handler(mWorker.getLooper(), callback);
                }else { //use external handler looper
                    mHandler = new Handler(mHandler.getLooper(), callback);
                }

                mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                mDate = new Date();
            }
        }

        @Override
        public void log(String tag, String message) {
            log(tag, message, null);
        }

        @Override
        public void log(String tag, String message, Throwable throwable) {
            if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(message)) return;
            init();
            if (mHandler == null) {
                Log.e(TAG, "Logger is not working, may had quit!");
                return;
            }
            String buildMsg = build(tag, message, throwable);
            mHandler.obtainMessage(MSG_LOG, buildMsg).sendToTarget();
            if (mDebug){
                Log.i(TextUtils.isEmpty(tag) ? TAG : tag, buildMsg, throwable);
            }
        }

        @Override
        protected void onQuit() {
            super.onQuit();
            if (mHandler != null){
                if (!mUseExternalHandler) { //disable quit when use external handler
                    mHandler.obtainMessage(MSG_QUIT).sendToTarget();
                }
                mHandler = null;
            }
        }

        private String build(String tag, String message, Throwable throwable){
            StringBuilder builder = new StringBuilder();
            mDate.setTime(System.currentTimeMillis());
            builder.append(mDateFormat.format(mDate));
            builder.append(SPACE);
            if (!TextUtils.isEmpty(tag)){
                builder.append("[");
                builder.append(tag);
                builder.append("]");
            }

            Thread current = Thread.currentThread();
            builder.append("[");
            builder.append(current.getName());
            builder.append(',');
            builder.append(Process.myPid());
            builder.append('-');
            builder.append(Process.myTid());
            builder.append("]");

            if (!TextUtils.isEmpty(message)){
                builder.append(":");
                builder.append(SPACE);
                builder.append(message);
            }

            if (throwable != null){
                builder.append('\n');
                builder.append('\t');
                StringBuilderWriter sbw = new StringBuilderWriter(builder);
                PrintWriter pw = new PrintWriter(sbw);
                throwable.printStackTrace(pw);
            }

            return builder.toString();
        }
    }

}
