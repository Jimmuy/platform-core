package com.qcec.app;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.qcec.dataservice.service.ApiService;
import com.qcec.dataservice.service.HttpService;

public class QCTabActivity extends TabActivity{
	private HttpService httpService;
	private ApiService apiService;
	
	private TitleBar titleBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titleBar = initTitleBar();
		
		QCApplication.getInstance().activityOnCreate(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		QCApplication.getInstance().activityOnResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		QCApplication.getInstance().activityOnPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		QCApplication.getInstance().activityOnDestroy(this);
	}
	
	protected TitleBar initTitleBar() {
		return new TitleBar(this, TitleBar.NO_TITLE);
	}
	
	public TitleBar getTitleBar() {
		return titleBar;
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
		return QCApplication.getInstance().getService(name);
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
