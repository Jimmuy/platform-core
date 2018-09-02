package com.qcec.image;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.qcec.app.QCApplication;
import com.qcec.io.FileManager;
import com.qcec.log.QCLog;
import com.qcec.utils.HexUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by sunyun on 16/6/24.
 */
public class ImageDiskCache implements DiskCache {

    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private volatile DiskLruCache diskCache;

    private final Object diskCacheLock = new Object();
    private File disCacheDir;

    private static class ImageDiskCacheHolder {
        public static ImageDiskCache instance = new ImageDiskCache();
    }

    public static ImageDiskCache getInstance() {
        return ImageDiskCacheHolder.instance;
    }

    private ImageDiskCache() {
        disCacheDir = FileManager.getDiskCacheDir(QCApplication.getInstance(), "image");
    }

    private DiskLruCache getDiskCache() throws IOException {
        if(diskCache == null || diskCache.isClosed()) {
            synchronized (ImageDiskCache.class) {
                if (diskCache == null || diskCache.isClosed()) {
                    File diskCacheDir = disCacheDir;
                    if (diskCacheDir != null) {
                        if (!diskCacheDir.exists()) {
                            diskCacheDir.mkdirs();
                        }
                        if (FileManager.hasEnoughSize(diskCacheDir.getPath(), DEFAULT_DISK_CACHE_SIZE)) {
                            diskCache = DiskLruCache.open(diskCacheDir, 1, 1,
                                    DEFAULT_DISK_CACHE_SIZE);
                        } else {
                            throw new IOException("have no usable space");
                        }
                    }
                }
            }
        }
        return diskCache;
    }

    @Override
    public File get(Key key) {
        final String cacheKey = hashKeyForDisk(key);
        File result = null;
        try {
            final DiskLruCache.Value value = getDiskCache().get(cacheKey);
            if (value != null) {
                result = value.getFile(0);
            }
            QCLog.d("Disk cache get: Key " + cacheKey);
        } catch (IOException e) {
            QCLog.e("getBitmapFromDiskCache - " + e);
        }
        return result;
    }

    @Override
    public void put(Key key, Writer writer) {
        final String cacheKey = hashKeyForDisk(key);
        synchronized (diskCacheLock) {
            try {
                DiskLruCache diskCache = getDiskCache();
                DiskLruCache.Value current = diskCache.get(cacheKey);
                if (current != null) {
                    return;
                }

                DiskLruCache.Editor editor = getDiskCache().edit(cacheKey);
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    try {
                        File file = editor.getFile(0);
                        if (writer.write(file)) {
                            editor.commit();
                        }
                    } finally {
                        editor.abortUnlessCommitted();
                    }
                }
                QCLog.d("Disk cache put: Key " + cacheKey);
            } catch (final IOException e) {
                QCLog.e("addBitmapToCache - " + e);
            }
        }
    }

    @Override
    public void delete(Key key) {
        String cacheKey = hashKeyForDisk(key);
        synchronized (diskCacheLock) {
            try {
                getDiskCache().remove(cacheKey);
                QCLog.d("Disk cache removed: Key " + key);
            } catch (IOException e) {
                QCLog.e("removeCache - " + e);
            }
        }
    }

    public long getDiskCacheSize() {
        if (diskCache != null) {
            return diskCache.size();
        }
        return 0;
    }

    @Override
    public void clear() {
        synchronized (diskCacheLock) {
            try {
                getDiskCache().delete();
                QCLog.d("Disk cache cleared");
            } catch (IOException e) {
                QCLog.e("clearCache - " + e);
            }
            diskCache = null;
        }
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable
     * for using as a disk filename.
     */
    private static String hashKeyForDisk(Key key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            key.updateDiskCacheKey(mDigest);
            cacheKey = HexUtils.bytesToHexString(mDigest.digest());
        } catch (Exception e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }


}
