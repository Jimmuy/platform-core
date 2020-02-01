package com.jimmy.debug.activity;

import android.os.Bundle;

import com.jimmy.app.CoreActivity;
import com.jimmy.app.CoreLoadingDialog;
import com.jimmy.app.ILoadingDialog;
import com.jimmy.core.R;
import com.jimmy.core.databinding.CrashDetailBinding;

import org.jetbrains.annotations.NotNull;

/**
 * 崩溃日志详情界面
 * Created by lorin on 16/2/14.
 */
public class DebugCrashDetailActivity extends CoreActivity<CrashDetailBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("崩溃日志详情");
        binding.crashInfoDetailTv.setText(getIntent().getStringExtra("crashInfo"));

    }

    @Override
    public int getLayoutId() {
        return R.layout.crash_info_detail_activity;
    }



}
