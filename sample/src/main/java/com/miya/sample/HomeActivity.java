package com.miya.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.miya.sample.databinding.HomeActivityBinding;
import com.jimmy.app.CoreActivity;
import com.jimmy.dataservice.base.RequestHandler;
import com.jimmy.dataservice.request.ApiRequest;
import com.jimmy.dataservice.request.BasicApiRequest;
import com.jimmy.dataservice.response.ApiResponse;
import com.jimmy.debug.DebugManager;

import org.json.JSONObject;


public class HomeActivity extends CoreActivity<HomeActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnClick(this);
        initTitle();


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initTitle() {
        setTitle("Home");
        setRightText("right");
        getRightView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("right");
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request:
                sendRequest();
                break;

            case R.id.btn_debug:
                initDebug();
                break;
        }
    }

    private void initDebug() {
        requestPermission();
        DebugManager.getInstance().setDebugMode(true);
    }

    private void sendRequest() {
        BasicApiRequest request = new BasicApiRequest(" https://www.easy-mock.com/mock/5c515611b1c1b9153666e243/example/test/get", "GET");
        getApiService().exec(request, new RequestHandler<ApiRequest, ApiResponse>() {
            @Override
            public void onRequestStart(ApiRequest req) {

            }

            @Override
            public void onRequestProgress(ApiRequest req, int count, int total) {

            }

            @Override
            public void onRequestFinish(ApiRequest req, ApiResponse resp) {
                binding.tvContent.setText(resp.getResultMap().toString());
                Toast.makeText(HomeActivity.this, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestFailed(ApiRequest req, ApiResponse resp) {
                Toast.makeText(HomeActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DebugManager.getInstance().setDebugMode(true);
                DebugManager.getInstance().getDebugAgent().setAgentTitle("debug");
            } else {
                Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugManager.getInstance().setDebugMode(false);
    }
}
