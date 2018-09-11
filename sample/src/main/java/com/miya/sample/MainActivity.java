package com.miya.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.miya.sample.databinding.HomeActivityBinding;
import com.qcec.app.QCActivity;
import com.qcec.dataservice.base.RequestHandler;
import com.qcec.dataservice.request.ApiRequest;
import com.qcec.dataservice.request.BasicApiRequest;
import com.qcec.dataservice.request.BasicHttpRequest;
import com.qcec.dataservice.response.ApiResponse;


public class MainActivity extends QCActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setOnClick(this);
    }

    @Override
    public void onClick(View view) {
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
                Toast.makeText(MainActivity.this,"xxxx",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestFailed(ApiRequest req, ApiResponse resp) {
                Toast.makeText(MainActivity.this,"aaaa",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
