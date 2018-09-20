package com.jimmy.log;

import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * log工具类,可以输出格式化的log,并打印格式化的jsonString
 * Created by lorin on 16/2/18.
 */
public final class SmartLogger {

    private SmartLogger() {
    }

    ;

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 3;

    /**
     * Log level
     */
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    /**
     * Method Count
     */
    private static int METHOD_COUNT = 3;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    /**
     * verbose log
     */
    public static void v(String message, Object... args) {
        log(VERBOSE, message, args);
    }

    /**
     * debug log
     */
    public static void d(String message, Object... args) {
        log(DEBUG, message, args);
    }

    /**
     * information log
     */
    public static void i(String message, Object... args) {
        log(INFO, message, args);
    }

    /**
     * warning log
     */
    public static void w(String message, Object... args) {
        log(WARN, message, args);
    }

    /**
     * error log
     */
    public static void e(String message, Object... args) {
        e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(ERROR, message, args);
    }

    public static void json(Object object) {
        json(toStringProtect(object));
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized static void log(int logLevel, String msg, Object... args) {
        String message = formatString(msg, args);

        logTopBorder(logLevel);
        logHeaderContent(logLevel, METHOD_COUNT);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (METHOD_COUNT > 0) {
                logDivider(logLevel);
            }
            logContent(logLevel, message);
            logBottomBorder(logLevel);
            return;
        }
        if (METHOD_COUNT > 0) {
            logDivider(logLevel);
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logLevel, new String(bytes, i, count));
        }
        logBottomBorder(logLevel);
    }

    private static void logTopBorder(int logType) {
        logChunk(logType, TOP_BORDER);
    }

    private static void logHeaderContent(int logLevel, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        logChunk(logLevel, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
        logDivider(logLevel);
        String level = "";

        int stackOffset = getStackOffset(trace);

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logLevel, builder.toString());
        }
    }

    private static void logBottomBorder(int logLevel) {
        logChunk(logLevel, BOTTOM_BORDER);
    }

    private static void logDivider(int logLevel) {
        logChunk(logLevel, MIDDLE_BORDER);
    }

    private static void logContent(int logLevel, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logLevel, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    private static void logChunk(int logLevel, String chunk) {
        switch (logLevel) {
            case ERROR:
                CoreLog.e(chunk);
                break;
            case INFO:
                CoreLog.i(chunk);
                break;
            case VERBOSE:
                CoreLog.v(chunk);
                break;
            case WARN:
                CoreLog.w(chunk);
                break;
            default:
                CoreLog.d(chunk);
                break;
        }
    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private static int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(Printer.class.getName()) && !name.equals(SmartLogger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    private static String toStringProtect(Object object) {
        if (null != object) {
            return object.toString();
        }

        CoreLog.e("[SmartLogger] THE OBJECT IS NULL");
        return "null";
    }
}
