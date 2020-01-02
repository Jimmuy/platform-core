package com.jimmy.sample;

import com.jimmy.debug.DebugManager;
import com.jimmy.app.CoreApplication;

public class BaseApplication extends CoreApplication {

    @Override
    public void init() {
        DebugManager.getInstance().setDebugActivity(HomeActivity.class);

    }
}
