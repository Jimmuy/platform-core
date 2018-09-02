package com.qcec.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lorin on 16/3/7.
 */
public class DeviceUtils {

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static int getAPIVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getManufacturer() {
        return Build.PRODUCT;
    }

    public static String getFingerPrint() {
        return Build.FINGERPRINT;
    }

    public static String getSerial() {
        return Build.SERIAL;
    }

    public static String getSystemVersion() {
        return Build.VERSION.INCREMENTAL;
    }

    private static PackageManager mPackageManager = null;
    private static PackageInfo mPackageInfo = null;

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {

            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /**
     * 获取App占用内存
     *
     * @param context
     * @return
     */
    public static int getAppMemorySize(Context context) {
        return getProcessMemorySize(context, android.os.Process.myPid());
    }

    /**
     * 获取进程占用内存
     *
     * @param context
     * @param pid
     * @return
     */
    public static int getProcessMemorySize(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] pids = new int[]{pid};
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(pids);
        memoryInfo[0].getTotalSharedDirty();
        int memSize = memoryInfo[0].getTotalPss();
        return memSize;
    }

    public static String getUUID(Context context){
        return DeviceUuidFactory.getUUID(context);
    }

    public static String getMD5(String content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}