package com.jimmy.sample

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast

import com.jimmy.app.CoreActivity
import com.jimmy.app.CoreLoadingDialog
import com.jimmy.app.ILoadingDialog
import com.jimmy.iot.net.callback.SimpleCallBack
import com.jimmy.iot.net.exception.ApiException
import com.jimmy.sample.databinding.HomeActivityBinding
import com.jimmy.debug.DebugManager
import com.jimmy.sample.net.DemoApiResult
import com.jimmy.sample.net.HttpManager


class HomeActivity : CoreActivity<HomeActivityBinding>(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = this
        initTitle()


    }

    public override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initStatusBar() {
        //这里可以自定义自己想要的状态栏
        //ImmersionBar.with(this)
        //                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
        //                .statusBarColor(R.color.colorPrimary)
        //                .init()
    }

    private fun initTitle() {
        //提供通用的标题和返回样式，可以自定义左边返回样式和右边操作按钮样式，如果需要自定义titlebar 可以禁用本身自带的再去自定义
        //设置title，如果不需要title也可以重写isShowTitleBar()方法让现实全屏，并配合ImmersionBar自定义状态栏是否显示
        title = "Home"
        //设置右边副标题
        setRightText("right")
        //设置右边副标题的点击事件
        getRightView()?.let { it.setOnClickListener { showToast("right") } }
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                //启动Activity让用户授权
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 100)
            }
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_request -> sendRequest()

            R.id.btn_debug -> initDebug()
        }
    }

    private fun initDebug() {
        requestPermission()
        DebugManager.getInstance().setDebugMode(true)
    }

    private fun sendRequest() {
        HttpManager.get("https://www.easy-mock.com/mock/5c515611b1c1b9153666e243/example/test/get/standard").execute(object : SimpleCallBack<Any>() {
            override fun onError(e: ApiException) {
                Toast.makeText(this@HomeActivity, "fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(o: Any) {
                Toast.makeText(this@HomeActivity, "success", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DebugManager.getInstance().setDebugMode(true)
                DebugManager.getInstance().debugAgent.setAgentTitle("debug")
            } else {
                Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast.LENGTH_SHORT).show()

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        DebugManager.getInstance().setDebugMode(false)
    }

    override fun getProgressDialog(): ILoadingDialog {
        return CoreLoadingDialog(this)
    }
}
