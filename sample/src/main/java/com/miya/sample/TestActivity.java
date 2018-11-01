package com.miya.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.jimmy.app.CoreActivity;
import com.jimmy.debug.DebugManager;
import com.miya.sample.databinding.HomeActivityBinding;


public class TestActivity extends CoreActivity<HomeActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnClick(this);
        initTitle();
        binding.btnTest.setVisibility(View.VISIBLE);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
    }


    @Override
    public void onClick(View view) {


    }


}
