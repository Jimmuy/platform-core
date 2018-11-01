package com.jimmy.app;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.core.R;
import com.jimmy.core.databinding.TitleBinding;
import com.jimmy.datamodel.GsonConverter;
import com.jimmy.dataservice.service.ApiService;
import com.jimmy.dataservice.service.HttpService;
import com.jimmy.utils.ActivityAnimationStyle;

import java.lang.reflect.Type;

/**
 * Base Activity extends fragment activity.
 * Contains basic service and widget.
 * T is Data Binding class name
 */
public abstract class CoreActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private HttpService httpService;
    private ApiService apiService;
    protected T binding;
    private TitleBinding titleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        CoreApplication.getInstance().activityOnCreate(this);
        initToolBar();
    }

    private void initToolBar() {
        if (isShowTitleBar()) {
            if (!(binding.getRoot() instanceof ViewGroup)) {
                throw new IllegalArgumentException("root view must be a view group");
            } else {
                titleBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.view_toolbar, null, false);
                titleBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ((ViewGroup) binding.getRoot()).addView(titleBinding.getRoot());
            }

            if (titleBinding.toolbar != null) {
                //将Toolbar显示到界面
                setSupportActionBar(titleBinding.toolbar);
                //设置默认的标题不显示
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            if (titleBinding.tvTitle != null) {
                //getTitle()的值是activity的android:lable属性值
                titleBinding.tvTitle.setText(getTitle());

            }
        }

    }

    /**
     * 默认显示title bar 子类可以复写去除title bar
     */
    protected boolean isShowTitleBar() {
        return true;
    }

    public <T> T getIntentData(Type type) {
        String json = getIntent().getStringExtra("data");
        if (!TextUtils.isEmpty(json)) {
            return GsonConverter.decode(json, type);
        }

        return null;
    }

    public void setResult(int code, Object data) {
        Intent intent = new Intent();
        intent.putExtra("data", GsonConverter.toJson(data));
        setResult(code, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoreApplication.getInstance().activityOnResume(this);
        CoreApplication.getInstance().setCurrentActivity(this);
        /**
         * 判断是否有Toolbar,并默认显示返回按钮
         */
        if (null != titleBinding && isShowBacking()) {
            showBack();
        }
    }

    public void finish(int slideStyle) {
        super.finish();

        setAnimationStyle(slideStyle);
    }

    @Override
    public void finish() {
        finish(ActivityAnimationStyle.STYLE_SLIDE_OUT);
    }

    private void setAnimationStyle(int slideStyle) {
        switch (slideStyle) {
            case ActivityAnimationStyle.STYLE_SLIDE_OUT:
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_OUT:
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                break;
            case ActivityAnimationStyle.STYLE_SLIDE_IN:
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_IN:
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_IN:
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_OUT:
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case ActivityAnimationStyle.STYLE_FADE_IN:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case ActivityAnimationStyle.STYLE_FADE_OUT:
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CoreApplication.getInstance().activityOnPause(this);
        if (this.equals(CoreApplication.getInstance().getCurrentActivity())) {
            CoreApplication.getInstance().setCurrentActivity(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreApplication.getInstance().activityOnDestroy(this);
        if (this.equals(CoreApplication.getInstance().getCurrentActivity())) {
            CoreApplication.getInstance().setCurrentActivity(null);
        }
    }

    private static final String TAG = CoreActivity.class.getSimpleName();


    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivity(Intent intent, int pushStyle) {
        startActivityForResult(intent, -1, pushStyle);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivityForResult(Intent intent, int requestCode, int pushStyle) {
        super.startActivityForResult(intent, requestCode);
        setAnimationStyle(pushStyle);
    }

    public void startActivity(String urlSchema) {
        startActivity(urlSchema, null, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivity(String urlSchema, Object data, int pushStyle) {
        startActivityForResult(urlSchema, data, -1, pushStyle);
    }

    public void startActivityForResult(String urlSchema, int requestCode) {
        startActivityForResult(urlSchema, null, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivityForResult(String urlSchema, Object data, int requestCode, int pushStyle) {
        if (TextUtils.isEmpty(urlSchema)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema));
        if (data != null) {
            if (data instanceof String) {
                intent.putExtra("data", (String) data);
            } else {
                intent.putExtra("data", GsonConverter.toJson(data));
            }
        }
        startActivityForResult(intent, requestCode, pushStyle);
    }

    public Object getService(String name) {
        return CoreApplication.getInstance().getService(name);
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

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getHeaderTitle() {
        return titleBinding.tvTitle;
    }

    public void setRightText(CharSequence rightTitle) {
        getRightView().setVisibility(View.VISIBLE);
        getRightView().setText(rightTitle);

    }

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getRightView() {
        return titleBinding.tvRightText;
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        if (titleBinding.tvTitle != null) {
            titleBinding.tvTitle.setText(title);
        }
    }


    /**
     * 版本号小于21的后退按钮图片
     */
    private void showBack() {
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        titleBinding.toolbar.setNavigationIcon(getLeftDrawable());
        titleBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 设置左边返回按钮图片资源
     */
    protected int getLeftDrawable() {
        return R.drawable.back;
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    protected boolean isShowBacking() {
        return true;
    }

    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     *
     * @return res layout xml id
     */
    protected abstract int getLayoutId();

    /**
     * 关闭软键盘
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 开启软键盘
     */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
