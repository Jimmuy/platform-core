package com.jimmy.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.jimmy.app.CoreActivity;
import com.jimmy.app.CoreLoadingDialog;
import com.jimmy.app.ILoadingDialog;
import com.jimmy.iot.net.callback.SimpleCallBack;
import com.jimmy.iot.net.exception.ApiException;
import com.jimmy.sample.databinding.HomeActivityBinding;
import com.jimmy.debug.DebugManager;
import com.jimmy.sample.net.DemoApiResult;
import com.jimmy.sample.net.HttpManager;


public class HomeActivity extends CoreActivity<HomeActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnClick(this);
        initTitle();


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initTitle() {
        //提供通用的标题和返回样式，可以自定义左边返回样式和右边操作按钮样式，如果需要自定义titlebar 可以禁用本身自带的再去自定义

        //设置title
        setTitle("Home");
        //设置右边副标题
        setRightText("right");
        //设置右边副标题的点击事件
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
        HttpManager.get("https://www.easy-mock.com/mock/5c515611b1c1b9153666e243/example/test/get/standard").execute(new SimpleCallBack<Object>() {
            @Override
            public void onError(ApiException e) {
                Toast.makeText(HomeActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object o) {
                Toast.makeText(HomeActivity.this, "success", Toast.LENGTH_SHORT).show();
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

    @Override
    public ILoadingDialog getProgressDialog() {
        return new CoreLoadingDialog(this);
    }
}
