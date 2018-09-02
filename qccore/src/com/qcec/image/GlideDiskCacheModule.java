package com.qcec.image;

import android.content.Context;
import android.media.Image;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by sunyun on 16/6/24.
 */
public class GlideDiskCacheModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(ImageDiskCache.getInstance());
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
