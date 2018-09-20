package com.qcec.dataservice.base;

import android.content.Context;

import com.qcec.dataservice.service.ApiService;
import com.qcec.dataservice.service.HttpService;
import com.qcec.log.CoreLog;

public class DataServiceManager {

    private HttpService httpService;
    private ApiService  apiService;

    private static DataServiceManager dataServiceManager = null;

    public static DataServiceManager getInstance() {
        if (dataServiceManager == null) {
            dataServiceManager = new DataServiceManager();
        }
        return dataServiceManager;
    }

    public synchronized DataService getService(Context context, String name) {
        if ("http".equals(name)) {
            if (httpService == null) {
                httpService = new HttpService(context);
            }
            return httpService;
        }
        if ("api".equals(name)) {
            if (apiService == null) {
                apiService = new ApiService(context);
            }
            return apiService;
        }

        CoreLog.e("unknown service \"" + name + "\"");
        return null;
    }

}
