package com.qcec.app;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.qcec.datamodel.GsonConverter;
import com.qcec.dataservice.base.DataService;
import com.qcec.dataservice.base.DataServiceManager;
import com.qcec.dataservice.service.ApiService;
import com.qcec.dataservice.service.HttpService;
import com.qcec.interfaces.IAccountManager;
import com.qcec.log.CoreLog;
import com.qcec.utils.SystemUtils;

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
