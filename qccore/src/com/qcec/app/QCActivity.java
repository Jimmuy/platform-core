package com.qcec.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.qcec.core.R;
import com.qcec.datamodel.GsonConverter;
import com.qcec.dataservice.service.ApiService;
import com.qcec.dataservice.service.HttpService;
import com.qcec.utils.ActivityAnimationStyle;
import com.qcec.log.QCLog;

import java.lang.reflect.Type;

import static android.R.attr.data;

/**
 * Base Activity extends fragment activity.
 * Contains basic service and widget.
 *
 */
public class QCActivity extends FragmentActivity {

    private HttpService httpService;
    private ApiService apiService;
    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar = initTitleBar();
        if (titleBar.getTitleStyle() == TitleBar.CUSTOM_TITLE) {
            titleBar.setTitle(getTitle());
            titleBar.setLeftView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(v);
                    QCActivity.this.finish();
                }
            });
        }
        QCApplication.getInstance().activityOnCreate(this);
    }

    public <T> T getIntentData(Type type) {
        String json = getIntent().getStringExtra("data");
        if(!TextUtils.isEmpty(json)) {
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
        QCApplication.getInstance().activityOnResume(this);
        QCApplication.getInstance().setCurrentActivity(this);
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
        QCApplication.getInstance().activityOnPause(this);
        if (this.equals(QCApplication.getInstance().getCurrentActivity())) {
            QCApplication.getInstance().setCurrentActivity(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QCApplication.getInstance().activityOnDestroy(this);
        if (this.equals(QCApplication.getInstance().getCurrentActivity())) {
            QCApplication.getInstance().setCurrentActivity(null);
        }
    }

    protected TitleBar initTitleBar() {
        return new TitleBar(this, TitleBar.CUSTOM_TITLE);
    }

    private static final String TAG = QCActivity.class.getSimpleName();

    public TitleBar getTitleBar() {
        return titleBar;
    }


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
        if(data != null) {
            if(data instanceof String) {
                intent.putExtra("data", (String)data);
            } else {
                intent.putExtra("data", GsonConverter.toJson(data));
            }
        }
        startActivityForResult(intent, requestCode, pushStyle);
    }

    public Object getService(String name) {
        return QCApplication.getInstance().getService(name);
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

}
