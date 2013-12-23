package com.liyun.waterfallView.tool;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-11-6
 */
public class Logger {
    static final private String TAG = "com.gracecode.android.presentation";

    static final public void e(String message) {
        log(Log.ERROR, message);
    }

    static final public void v(String message) {
        log(Log.VERBOSE, message);
    }

    static final public void i(String message) {
        log(Log.INFO, message);
    }


    public static void w(String s) {
        log(Log.WARN, s);
    }

    static final private int log(int level, String message) {
        if (Constants.DEBUG) {
            switch (level) {
                case Log.ERROR:
                    return Log.e(TAG, message);

                case Log.INFO:
                    return Log.i(TAG, message);

                case Log.WARN:
                    return Log.w(TAG, message);
            }
        }

        return Log.v(TAG, message);
    }
}
