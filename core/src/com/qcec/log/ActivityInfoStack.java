package com.qcec.log;

import android.app.Activity;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ActivityInfoStack {

    static LinkedList<ActivityInfo> list = new LinkedList();

    public static void add(Activity activity) {
        ActivityInfo info = new ActivityInfo();
        info.activityName = activity.getLocalClassName();
        info.entryTime = new Date().getTime();
        list.add(info);
    }

    public static List<ActivityInfo> listAll() {
        return (List<ActivityInfo>) list.clone();
    }

    public static String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            ActivityInfo info = list.get(i);
            if (i != 0) {
                sb.append("\n");
            }
            sb.append((i + 1) + "、 " + info.activityName + ":" + DateFormat.format("yyyy-MM-dd kk:mm:ss", info.entryTime));
        }
        return sb.toString();
    }

    public static class ActivityInfo {

        public String activityName;

        public long entryTime;
    }
}
