package com.qcec.image;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;

/**
 * Created by sunyun on 16/6/23.
 */
public class ImageLoadOptions {

    public static final int IMAGE_SIZE_75 = 75;
    public static final int IMAGE_SIZE_100 = 100;
    public static final int IMAGE_SIZE_150 = 150;
    public static final int IMAGE_SIZE_200 = 200;
    public static final int IMAGE_SIZE_240 = 240;
    public static final int IMAGE_SIZE_320 = 320;
    public static final int IMAGE_SIZE_480 = 480;
    public static final int IMAGE_SIZE_800 = 800;

    private int imageOnLoading;

    private int imageForEmpty;

    private int imageOnFail;

    private boolean cacheInMemory = true;

    private boolean cacheOnDisk = true;

    private int width = -1;

    private int height = -1;

    private ImageLoadOptions(Builder builder) {
        this.imageOnLoading = builder.imageOnLoading;
        this.imageForEmpty = builder.imageForEmpty;
        this.imageOnFail = builder.imageOnFail;
        this.cacheInMemory = builder.cacheInMemory;
        this.cacheOnDisk = builder.cacheOnDisk;
        this.width = builder.width;
        this.height = builder.height;
    }

    public boolean shouldShowImageOnLoading() {
        return imageOnLoading != 0;
    }

    public boolean shouldShowImageForEmpty() {
        return imageForEmpty != 0;
    }

    public boolean shouldShowImageOnFail() {
        return imageOnFail != 0;
    }

    public int getImageOnLoading() {
        return imageOnLoading;
    }

    public int getImageForEmpty() {
        return imageForEmpty;
    }

    public int getImageOnFail() {
        return imageOnFail;
    }

    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    public boolean isCacheOnDisk() {
        return cacheOnDisk;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static class Builder {

        private int imageOnLoading;

        private int imageForEmpty;

        private int imageOnFail;

        private boolean cacheInMemory = true;

        private boolean cacheOnDisk = true;

        private int width = -1;

        private int height = -1;

        public Builder setImageOnLoading(int resId) {
            imageOnLoading = resId;
            return this;
        }

        public Builder setImageForEmpty(int resId) {
            imageForEmpty = resId;
            return this;
        }

        public Builder setImageOnFail(int resId) {
            imageOnFail = resId;
            return this;
        }

        public Builder cacheInMemory(boolean cacheInMemory) {
            this.cacheInMemory = cacheInMemory;
            return this;
        }

        public Builder cacheOnDisk(boolean cacheOnDisk) {
            this.cacheOnDisk = cacheOnDisk;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public ImageLoadOptions build() {
            return new ImageLoadOptions(this);
        }

    }
}
