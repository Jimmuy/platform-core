package com.jimmy.log;

import android.graphics.Color;
import android.util.Log;

import com.jimmy.debug.DebugConsole;
import com.jimmy.debug.DebugManager;

/**
 * 用来在Debug模式中在console控制台上打印log
 */
public final class ConsoleLog {
    private ConsoleLog() {
    }

    /**
     * verbose log
     */
    public static void v(String message, Object... args) {
        log(Log.VERBOSE, message, args);
    }

    /**
     * debug log
     */
    public static void d(String message, Object... args) {
        log(Log.DEBUG, message, args);
    }

    /**
     * information log
     */
    public static void i(String message, Object... args) {
        log(Log.INFO, message, args);
    }

    /**
     * warning
     */
    public static void w(String message, Object... args) {
        log(Log.WARN, message, args);
    }

    /**
     * error log
     */
    public static void e(String message, Object... args) {
        log(Log.ERROR, message, args);
    }

    private static void log(int level, String message, Object... args) {
        DebugConsole console = DebugManager.getInstance().getDebugConsole();
        if (console != null) {
            int color = Color.WHITE;
            switch (level) {
                case Log.WARN:
                    color = Color.BLUE;
                    break;
                case Log.ERROR:
                    color = Color.RED;
                    break;
            }
            console.printText(color, formatString(message, args));
        }
    }

    private static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }
}
