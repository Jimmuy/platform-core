package com.jimmy.io;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.jimmy.utils.VersionUtils;

import java.io.File;

public class FileManager {

    public enum StorageType {
        Data,
        SDCard
    }

    public static boolean existSDCard() {
        return Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState());
    }

    public static boolean checkSDCardWritePermission(Context context) {
        if (PackageManager.PERMISSION_GRANTED !=
                context.getPackageManager().checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        context.getApplicationContext().getPackageName())) {
            return false;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isSDCardRemovable() {
        if (VersionUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getRootFilesDir(Context context, StorageType type) {
        if(type == StorageType.Data) {
            return getDataFilesDir(context);

        }

        return getExternalFilesDir(context);
    }

    public static File getDataFilesDir(Context context) {
        return context.getApplicationContext().getFilesDir();
    }

    public static File getExternalFilesDir(Context context) {
        String path = Environment.getExternalStorageDirectory() + File.separator
                + context.getApplicationContext().getPackageName();

        if(FileUtils.isFileExist(path)) {
            FileUtils.deleteFile(path);
        }

        if(!FileUtils.isFolderExist(path)) {
            FileUtils.makeFolders(path);
        }

        return new File(path);
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {

        final String cachePath = existSDCard() || !isSDCardRemovable() ? getExternalCacheDir(
                context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (VersionUtils.hasFroyo()) {

            return context.getExternalCacheDir();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /**
     * 文件夹总大小
     * @param path 文件路径（必须为文件夹）
     */
    public static long sizeOfByAndroidStatFs(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long aBlocks = statFs.getBlockCount();
        long aBlockSum = blockSize * aBlocks;
        return aBlockSum;
    }

    /**
     * 文件夹可用空间大小
     * @param path 文件路径（必须为文件夹）
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long sizeOfFreeByAndroidStatFs(String path) {
        if (VersionUtils.hasGingerbread()) {
            File file = new File(path);
            return file.getUsableSpace();
        }

        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long aBlocks = statFs.getAvailableBlocks();
        long aBlockSum = blockSize * aBlocks;
        return aBlockSum;
    }

    /**
     * @param path absolute path
     * @param size given enough size
     * @return 如果指定路径的可用存储空间大于size，返回TURE
     */
    public static boolean hasEnoughSize(String path, long size) {
        return sizeOfFreeByAndroidStatFs(path) >= size;
    }
}
