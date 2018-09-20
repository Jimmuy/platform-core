package com.miya.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.miya.sample.databinding.HomeActivityBinding;
import com.qcec.app.CoreActivity;
import com.qcec.dataservice.base.RequestHandler;
import com.qcec.dataservice.request.ApiRequest;
import com.qcec.dataservice.request.BasicApiRequest;
import com.qcec.dataservice.response.ApiResponse;
import com.qcec.debug.DebugManager;


public class HomeActivity extends CoreActivity<HomeActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnClick(this);
        requestPermission();


    }

    private void initTitle() {
        setTitle("title");
        setRightText("right");
        getRightView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("right");
            }
        });
        initTitle()
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
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View view) {
        DebugManager.getInstance().setDebugMode(true);
        DebugManager.getInstance().getDebugAgent().setAgentTitle("xxxx");
        BasicApiRequest request = new BasicApiRequest("http://joyoung-china.digilinx.net.cn/script/d_QuerySeedInfo.php", "GET");
        getApiService().exec(request, new RequestHandler<ApiRequest, ApiResponse>() {
            @Override
            public void onRequestStart(ApiRequest req) {

            }

            @Override
            public void onRequestProgress(ApiRequest req, int count, int total) {

            }

            @Override
            public void onRequestFinish(ApiRequest req, ApiResponse resp) {
                Toast.makeText(HomeActivity.this, "xxxx", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestFailed(ApiRequest req, ApiResponse resp) {
                Toast.makeText(HomeActivity.this, "aaaa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DebugManager.getInstance().setDebugMode(true);
                DebugManager.getInstance().getDebugAgent().setAgentTitle("xxxx");
            } else {
                Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast.LENGTH_SHORT).show();
                ;
            }
        }

    }
}
