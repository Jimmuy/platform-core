package com.qcec.debug;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qcec.core.R;
import com.qcec.utils.DeviceUtils;
import com.qcec.utils.VersionUtils;

import java.text.DecimalFormat;

/**
 * Created by sunyun on 16/3/27.
 */
public class DebugAgent {

    Context appContext;
    WindowManager windowManager;
    WindowManager.LayoutParams params;
    GestureDetector gestureDetector;

    View contentView;
    TextView debugTitle;
    TextView statusText;

    Rect frame = new Rect();
    DecimalFormat decimalFormat = new DecimalFormat("0.#");

    public DebugAgent(Context context) {
        appContext = context;
        windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        initView();
        monitorMemory();
    }

    private void initView() {
        gestureDetector = new GestureDetector(appContext, new DebugAgentOnGestureListener());
        contentView = LayoutInflater.from(appContext).inflate(R.layout.debug_agent, null);
        statusText = (TextView) contentView.findViewById(R.id.debug_status);
        debugTitle = (TextView) contentView.findViewById(R.id.debug_title);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                VersionUtils.hasKitKat() ? WindowManager.LayoutParams.TYPE_TOAST :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);
        params.gravity = Gravity.CENTER;
        params.x = windowManager.getDefaultDisplay().getWidth() / 2 - getDimensionPixelSize(R.dimen.debug_agent_params_x);
        params.y = getDimensionPixelSize(R.dimen.debug_agent_params_y);
        windowManager.addView(contentView, params);
        contentView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        contentView.getWindowVisibleDisplayFrame(frame);
                        params.x = (int) (event.getRawX() - frame.centerX());
                        params.y = (int) (event.getRawY() - frame.centerY());
                        windowManager.updateViewLayout(contentView, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x < 0) {
                            params.x = -windowManager.getDefaultDisplay().getWidth() / 2 + contentView.getMeasuredWidth() / 2 + getDimensionPixelSize(R.dimen.debug_agent_params_10);
                        } else {
                            params.x = windowManager.getDefaultDisplay().getWidth() / 2 - contentView.getMeasuredWidth() / 2 - getDimensionPixelSize(R.dimen.debug_agent_params_10);
                        }
                        windowManager.updateViewLayout(contentView, params);
                        break;
                }
                return true;
            }
        });

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

    private class DebugAgentOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            DebugManager.getInstance().startDebugActivity();
            return super.onDoubleTap(e);
        }
    }
}
