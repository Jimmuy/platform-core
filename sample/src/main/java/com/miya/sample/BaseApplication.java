package com.miya.sample;

import com.jimmy.app.CoreApplication;
import com.jimmy.debug.DebugManager;

public class BaseApplication extends CoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugManager.getInstance().setDebugActivity(HomeActivity.class);
    }
}
