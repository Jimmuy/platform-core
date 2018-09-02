package com.qcec.debug;

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcec.core.R;
import com.qcec.utils.VersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lorin on 16/3/25.
 */
public class DebugConsole {

    private static final int MAX_BUFFER_COUNT = 12;

    Context appContext;

    //定义浮动窗口布局
    LinearLayout contentView;
    TextView printTextView;

    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager windowManager;

    List<String> printBuffer = new ArrayList<>();
    StringBuilder strBuilder = new StringBuilder();

    public DebugConsole(Context context) {
        appContext = context;
        createFloatView();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) appContext.getSystemService(appContext.WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        wmParams.gravity = Gravity.BOTTOM;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        contentView = (LinearLayout) LayoutInflater.from(appContext).inflate(R.layout.debug_console, null);
        printTextView = (TextView) contentView.findViewById(R.id.printTextView);
        windowManager.addView(contentView, wmParams);
    }

    /**
     * color不支持透明度
     */
    public void printText(int color, String message) {
        String str = String.format("<font color=\'#%06x\'>%s</font><br/>", (color & 0x00ffffff), message);
        printBuffer.add(0, str);

        refresh();
    }

    private void refresh() {
        if (printBuffer.size() > MAX_BUFFER_COUNT) {
            printBuffer.remove(printBuffer.size() - 1);
        }

        for (String str : printBuffer) {
            strBuilder.append(str);
        }

        printTextView.setText(Html.fromHtml(strBuilder.toString()));
        strBuilder.setLength(0);
    }

    public void recycle() {
        if (contentView != null) {
            windowManager.removeView(contentView);
            contentView = null;
        }
    }
}
