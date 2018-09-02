package com.qcec.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import java.io.IOException;

/**
 * 从SDCard异步加载图片
 * <p/>
 * Created by yanghui on 15-10-19.
 */
public class ImageUtil {

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight, int orientation) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = null;
        int count = 0;
        while (bitmap == null && count < 5) {
            try {
                bitmap = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError e) {
                reqWidth /= 2;
                reqHeight /= 2;
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            }
            count++;
        }
        /**
         * 把图片旋转为正的方向,orientation为0则不需要
         */
        if (orientation != 0) {
            return rotaingImageView(orientation, bitmap);
        } else {
            return bitmap;
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int size, reqSize;
        double ratioX = (double) width / reqWidth;
        double ratioY = (double) height / reqHeight;
        if (ratioX > ratioY) {
            size = width;
            reqSize = reqWidth;
        } else {
            size = height;
            reqSize = reqHeight;
        }

        while ((size / inSampleSize) > (reqSize * 1.6)) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片的旋转的角度，还是三星的问题，需要根据图片的旋转角度正确显示
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
