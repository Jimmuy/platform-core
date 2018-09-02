package com.qcec.debug;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qcec.app.QCApplication;
import com.qcec.log.analysis.AnalysisService;
import com.qcec.utils.PreferenceUtils;
import com.qcec.utils.SystemUtils;


/**
 * Debug的管理类
 * Created by jimmy on 16/2/22.
 */
public class DebugManager {

    private static volatile DebugManager instance;

    private Context appContext;

    private DebugAgent agent;
    private DebugConsole console;

    private String activity;

    private DebugManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * 获取DebugManager的实例(需要在DebugManager初始化的前提下获取)
     */
    public static DebugManager getInstance() {
        if (instance == null) {
            synchronized (DebugManager.class) {
                if(instance == null) {
                    instance = new DebugManager(QCApplication.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * 设置 Debug Activity
     * @param cls
     */
    public void setDebugActivity(Class<?> cls) {
        activity = cls.getName();
    }

    public void startDebugActivity() {
        if(!TextUtils.isEmpty(activity)) {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(appContext, activity);
            intent.setComponent(comp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(intent);
        }
    }


    /**
     * 获取DebugAgent实例
     *
     * @return DebugAgent实例
     */
    public DebugAgent getDebugAgent() {
        return agent;
    }

    /**
     * 是否处于Debug模式
     *
     * @return boolean
     */
    public boolean isDebug() {
        return PreferenceUtils.getPrefBoolean(appContext, "debug_mode", SystemUtils.isDebuggable(appContext));
    }

    /**
     * 设置是否打开Debug模式
     */
    public void setDebugMode(boolean isOpen) {
        if (isOpen) {
            startDebugAgent();
        } else {
            stopDebugAgent();
        }
        PreferenceUtils.setPrefBoolean(appContext, "debug_mode", isOpen);
    }

    /**
     * 获取DebugConsole实例
     *
     * @return DebugConsole实例
     */
    public DebugConsole getDebugConsole() {
        return console;
    }

    /**
     * 获取DebugConsole是否打开
     *
     * @return boolean
     */
    public boolean isDebugConsoleEnable() {
        return PreferenceUtils.getPrefBoolean(appContext, "console_switch", false);
    }

    /**
     * 设置DebugConsole打开或关闭
     */
    public void setDebugConsoleEnable(boolean enable) {
        PreferenceUtils.setPrefBoolean(appContext, "console_switch", enable);
        if (enable) {
            openDebugConsole();
        } else {
            closeDebugConsole();
        }
    }

    /**
     * 开启debug浮窗
     */
    private void startDebugAgent() {
        if (agent == null) {
            agent = new DebugAgent(appContext);
        }
    }

    /**
     * 关闭debug浮窗
     */
    private void stopDebugAgent() {
        if (agent != null) {
            agent.recycle();
            agent = null;
        }
    }

    /**
     * 打开debug控制台
     */
    private void openDebugConsole() {
        if (console == null) {
            console = new DebugConsole(appContext);
        }
    }

    /**
     * 关闭Debug控制台
     */
    private void closeDebugConsole() {
        if (console != null) {
            console.recycle();
            console = null;
        }
    }

    /**
     * 设置埋点模式开关
     */
    public void setAnalysisDebugMode(boolean isOpen) {
        AnalysisService.enableDebug(isOpen);
    }

    /**
     * 判断埋点Debug模式是否打开
     */
    public boolean isAnalysisDebug() {
        return AnalysisService.isDebug;
    }


    public void onApplicationResume() {
        if (isDebug()) {
            startDebugAgent();
        }

        if (isDebugConsoleEnable()) {
            openDebugConsole();
        }
    }

    public void onApplicationPause() {
        stopDebugAgent();
        closeDebugConsole();
    }

}
