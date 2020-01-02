package com.jimmy.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.WindowManager



import java.util.Hashtable

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)

private var _loadFactor: Int? = null
private var displayDensity = 0.0f


/**
 * 将px值转换为sp值，保证文字大小不变
 *
 * @param pxValue
 * @param context （DisplayMetrics类中属性scaledDensity）
 * @return
 */
fun px2sp(context: Context, pxValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * dp转px
 *
 * @param context
 * @param dp
 * @return
 */
fun dpToPixel(context: Context?, dp: Float): Int {
    return (dp * (getDisplayMetrics(context).densityDpi / 160f)).toInt()
}

/**
 * px转dp
 *
 * @param context
 * @param f
 * @return
 */
fun pixelsToDp(context: Context, f: Float): Float {
    return f / (getDisplayMetrics(context).densityDpi / 160f)
}

fun getDefaultLoadFactor(context: Context): Int {
    if (_loadFactor == null) {
        val integer = Integer.valueOf(
            0xf and context
                .resources.configuration.screenLayout
        )
        _loadFactor = integer
        _loadFactor = Integer.valueOf(Math.max(integer.toInt(), 1))
    }
    return _loadFactor!!.toInt()
}

/**
 * 得到屏幕密度
 *
 * @param context
 * @return
 */
fun getDensity(context: Context): Float {
    if (displayDensity.toDouble() == 0.0)
        displayDensity = getDisplayMetrics(context).density
    return displayDensity
}

fun getDisplayMetrics(context: Context?): DisplayMetrics {
    val displaymetrics = DisplayMetrics()
    (context?.getSystemService(
        Context.WINDOW_SERVICE
    ) as WindowManager).defaultDisplay.getMetrics(
        displaymetrics
    )
    return displaymetrics
}

/**
 * 屏幕高度
 *
 * @param context
 * @return
 */
fun getScreenHeight(context: Context): Float {
    return getDisplayMetrics(context).heightPixels.toFloat()
}

/**
 * 屏幕宽度
 *
 * @param context
 * @return
 */
fun getScreenWidth(context: Context): Float {
    return getDisplayMetrics(context).widthPixels.toFloat()
}


/**
 * 获取版本号
 *
 * @param context
 * @return
 */
fun getVersionCode(context: Context?): Int {
    var versionCode = 0
    try {
        if (context != null) {
            versionCode = context.packageManager
                .getPackageInfo(
                    context.packageName,
                    0
                ).versionCode
        }
    } catch (ex: PackageManager.NameNotFoundException) {
        versionCode = 0
    }

    return versionCode
}


/**
 * 获取版本名
 *
 * @param context
 * @return
 */
fun getVersionName(context: Context?): String {
    return try {
        context?.packageManager?.getPackageInfo(
            context.packageName,
            0
        )?.versionName ?: ""
    } catch (ex: PackageManager.NameNotFoundException) {
        ""
    }
}

fun isScreenOn(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isScreenOn
}


/**
 * 拨打电话
 *
 * @param context
 * @param number
 */
fun openDial(context: Context, number: String) {
    val uri = Uri.parse("userPhone:$number")
    val it = Intent(Intent.ACTION_DIAL, uri)
    context.startActivity(it)
}

@Throws(WriterException::class)
fun createQRCode(str: String, widthAndHeight: Int): Bitmap {
    val hints = Hashtable<EncodeHintType, String>()
    hints[EncodeHintType.CHARACTER_SET] = "utf-8"
    val matrix = MultiFormatWriter().encode(
        str,
        BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight
    )
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (matrix.get(x, y)) {
                pixels[y * width + x] = BLACK
            } else {
                pixels[y * width + x] = WHITE
            }
        }
    }
    val bitmap = Bitmap.createBitmap(
        width, height,
        Bitmap.Config.RGB_565
    )
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}


/**
 * if [T] is null run [nullTerm] else [term]
 */
fun <T, R> T?.forObj(term: (t: T) -> R, nullTerm: () -> R): R {
    return this.let {
        if (null == it) nullTerm() else term(it!!)
    }
}

/**
 * if [T] is null return [nullObj] else [obj]
 */
fun <T, R> T?.forObj(obj: R, nullObj: R): R {
    return this.let {
        if (null == it) nullObj else obj
    }
}

/**
 * if term is true run [trueTerm] else [falseTerm]
 */
fun <R> (() -> Boolean).doJudge(trueTerm: () -> R, falseTerm: () -> R): R {
    return if (this()) trueTerm() else falseTerm()
}

/**
 * if true run [trueTerm] else [falseTerm]
 */
fun <R> Boolean.doJudge(trueTerm: () -> R, falseTerm: () -> R): R {
    return if (this) trueTerm() else falseTerm()
}

/**
 * if true return [trueObj] else [falseObj]
 */
fun <R> Boolean.doJudge(trueObj: R, falseObj: R): R {
    return if (this) trueObj else falseObj
}

