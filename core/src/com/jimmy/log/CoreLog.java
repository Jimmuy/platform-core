package com.jimmy.log;

import android.util.Log;

/**
 * log工具类,打印类型和系统提供的log工具相同
 */
public final class CoreLog {

    private CoreLog() {
    }

    private static String TAG = "core";

    public static boolean isPrint = true;

    /**
     * verbose log
     */
    public static void v(String message, Object... args) {
        if (isPrint && message != null) {
            Log.v(TAG, formatString(message, args));
        }
    }

    /**
     * debug log
     */
    public static void d(String message, Object... args) {
        if (isPrint && message != null) {
            Log.d(TAG, formatString(message, args));
        }
    }

    /**
     * information log
     */
    public static void i(String message, Object... args) {
        if (isPrint && message != null) {
            Log.i(TAG, formatString(message, args));
        }
    }

    /**
     * warning log
     */
    public static void w(String message, Object... args) {
        if (isPrint && message != null) {
            Log.w(TAG, formatString(message, args));
        }
    }

    /**
     * error log
     */
    public static void e(String message, Object... args) {
        if (isPrint && message != null) {
            Log.e(TAG, formatString(message, args));
        }
    }

    private static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

}
