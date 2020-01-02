package com.jimmy.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.util.Iterator;
import java.util.List;

public class SystemUtils {

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getPackageName(Context context) {
        String pkgName = context.getPackageName();
        String[] nameArray = pkgName.split("\\.");
        if (nameArray.length > 0) {
            return nameArray[nameArray.length - 1];
        }
        return null;
    }


    public static boolean isDebuggable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        }
        return false;
    }

}
