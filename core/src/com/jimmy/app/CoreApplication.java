package com.jimmy.app;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.jimmy.datamodel.GsonConverter;
import com.jimmy.dataservice.base.DataService;
import com.jimmy.dataservice.base.DataServiceManager;
import com.jimmy.dataservice.service.ApiService;
import com.jimmy.dataservice.service.HttpService;
import com.jimmy.interfaces.IAccountManager;
import com.jimmy.log.CoreLog;
import com.jimmy.utils.SystemUtils;

import java.util.Map;

/**
 * Application with life cycle manager.
 *
 */

public class CoreApplication extends Application {

    private static CoreApplication instance;
    private DataServiceManager serviceManager;
    private HttpService httpService;
    private ApiService apiService;
    private IAccountManager accountManager;

    private Activity currentActivity;

    /**
     * Application life cycle manager
     */
    private static int liveCounter;
    private static int activeCounter;


    public static CoreApplication getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Application has not been created");
        }

        return instance;
    }

    /**
     * Your SHOULDN'T NEVER call this method yourself!
     */
    public CoreApplication() {
        instance = this;
    }

    public void setAccountManager(IAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public IAccountManager getAccountManager() {
        return accountManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!SystemUtils.isDebuggable(this)) {
            CoreLog.isPrint = false;
        }

        //Gson. Deserialize integers as integers and not as doubles
        GsonConverter.configTypeAdapter(new TypeToken<Map<String, Object>>() {
        }.getType(), new MapDeserializerDoubleAsIntFix());
    }

    public void onApplicationStart() {
        CoreLog.i("CoreApplication::onApplicationStart");
    }

    public void onApplicationResume() {
        CoreLog.i("CoreApplication::onApplicationResume");
    }

    public void onApplicationPause() {
        CoreLog.i("CoreApplication::onApplicationPause");

    }

    public void onApplicationStop() {
        CoreLog.i("CoreApplication::onApplicationStop");

    }


    public DataService getService(String name) {
        if (serviceManager == null) {
            serviceManager = DataServiceManager.getInstance();
        }
        return serviceManager.getService(this, name);
    }

    public HttpService getHttpService() {
        if (httpService == null) {
            httpService = (HttpService) getService("http");
        }
        return httpService;
    }

    public ApiService getApiService() {
        if (apiService == null) {
            apiService = (ApiService) getService("api");
        }
        return apiService;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if ((--activeCounter) == 0) {
                    CoreApplication.getInstance().onApplicationPause();
                }
            }
        }
    };


    public void activityOnCreate(Activity a) {
        if (liveCounter++ == 0) {
            onApplicationStart();
        }
    }

    public void activityOnResume(Activity a) {
        if (activeCounter++ == 0) {
            onApplicationResume();
        }
    }

    public void activityOnPause(Activity a) {
        handler.sendEmptyMessageDelayed(1, 100);
    }

    public void activityOnDestroy(Activity a) {
        if ((--liveCounter) == 0) {
            onApplicationStop();
        }
    }


}
