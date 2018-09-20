package com.qcec.app;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.qcec.dataservice.service.ApiService;
import com.qcec.dataservice.service.HttpService;

public class CoreTabActivity extends TabActivity {
    private HttpService httpService;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreApplication.getInstance().activityOnCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoreApplication.getInstance().activityOnResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CoreApplication.getInstance().activityOnPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreApplication.getInstance().activityOnDestroy(this);
    }

    public void startActivity(String urlSchema) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)));
    }

    public void startActivityForResult(String urlSchema, int requestCode) {
        startActivityForResult(
                new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)),
                requestCode);
    }

    public Object getService(String name) {
        return CoreApplication.getInstance().getService(name);
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
}
