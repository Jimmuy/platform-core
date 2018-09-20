package com.miya.sample;

import com.qcec.app.CoreApplication;
import com.qcec.debug.DebugManager;

public class BaseApplication extends CoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugManager.getInstance().setDebugActivity(HomeActivity.class);
    }
}
