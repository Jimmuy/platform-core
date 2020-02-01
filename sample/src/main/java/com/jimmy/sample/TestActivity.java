package com.jimmy.sample;

import android.os.Bundle;
import android.view.View;


import com.jimmy.app.CoreActivity;
import com.jimmy.app.CoreLoadingDialog;
import com.jimmy.app.ILoadingDialog;
import com.jimmy.sample.databinding.HomeActivityBinding;


public class TestActivity extends CoreActivity<HomeActivityBinding> implements View.OnClickListener {
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
