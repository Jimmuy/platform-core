package com.jimmy.debug;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jimmy.core.R;
import com.jimmy.utils.DeviceUtils;
import com.jimmy.utils.VersionUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class DebugAgent implements View.OnTouchListener {

    Context appContext;
    WindowManager windowManager;
    WindowManager.LayoutParams params;

    View contentView;
    TextView debugTitle;
    TextView statusText;

    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;

    private int sysBarHeight = 0;
    private long touchTime = 0;


    DecimalFormat decimalFormat = new DecimalFormat("0.#");

    public DebugAgent(Context context) {
        appContext = context;
        initView();
        monitorMemory();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(appContext);
        if (inflater == null)
            return;

        contentView = inflater.inflate(R.layout.debug_agent, null);
        contentView.setOnTouchListener(this);
        statusText = contentView.findViewById(R.id.debug_status);
        debugTitle = contentView.findViewById(R.id.debug_title);

        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0新特性
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //需要动态权限Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            params.type = WindowManager.LayoutParams.TYPE_PHONE;

        } else {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.START | Gravity.TOP;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;


        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        params.x = metrics.widthPixels;
        params.y = metrics.heightPixels / 2 - getSysBarHeight(appContext);
        windowManager.addView(contentView, params);
    }

    private int getDimensionPixelSize(int id) {
        return appContext.getResources().getDimensionPixelSize(id);
    }

    public void setAgentTitle(String title) {
        debugTitle.setText(title);
    }

    public void monitorMemory() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (contentView == null)
                    return;

                int memSize = DeviceUtils.getAppMemorySize(appContext);
                statusText.setText(decimalFormat.format(memSize / 1024.0) + "M");
                handler.postDelayed(this, 1000l);
            }
        });

    }

    public void recycle() {
        if (contentView != null) {
            windowManager.removeView(contentView);
            contentView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return floatLayoutTouch(event);
    }


    private boolean floatLayoutTouch(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                mInViewX = motionEvent.getX();
                mInViewY = motionEvent.getY();
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY() - getSysBarHeight(appContext);
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(appContext);
                break;
            case MotionEvent.ACTION_MOVE:
                // 更新浮动窗口位置参数
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(appContext);
                params.x = (int) (mInScreenX - mInViewX);
                params.y = (int) (mInScreenY - mInViewY);
                // 手指移动的时候更新小悬浮窗的位置
                windowManager.updateViewLayout(contentView, params);
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mDownInScreenX == mInScreenX && mDownInScreenY == mInScreenY) {
                    //第二次点击小与500毫秒
                    if (System.currentTimeMillis() - touchTime < 500) {
                        DebugManager.getInstance().startDebugActivity();
                        touchTime = 0;
                    }
                    touchTime = System.currentTimeMillis();
                }
                break;
        }
        return true;
    }

    // 获取系统状态栏高度
    public int getSysBarHeight(Context contex) {
        if (sysBarHeight > 0)
            return sysBarHeight;

        Class<?> c;
        Object obj;
        Field field;
        int x;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sysBarHeight = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sysBarHeight;
    }

}
