package com.miya.sample;

import com.qcec.app.QCActivity;
import com.qcec.app.QCApplication;
import com.qcec.debug.DebugManager;

public class BaseApplication extends QCApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugManager.getInstance().setDebugActivity(MainActivity.class);
    }
}
