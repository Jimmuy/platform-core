package com.qcec.debug.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.qcec.app.QCActivity;
import com.qcec.core.R;

/**
 * 崩溃日志详情界面
 * Created by lorin on 16/2/14.
 */
public class DebugCrashDetailActivity extends QCActivity {


    TextView crashInfoDetailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_info_detail_activity);
//        getTitleBar().setTitle("崩溃日志详情");

        crashInfoDetailTv = (TextView) findViewById(R.id.crash_info_detail_tv);
        crashInfoDetailTv.setText(getIntent().getStringExtra("crashInfo"));

    }



}
