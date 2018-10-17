package com.jimmy.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.jimmy.core.R;
import com.jimmy.dataservice.service.ApiService;
import com.jimmy.dataservice.service.HttpService;
import com.jimmy.utils.ActivityAnimationStyle;
import com.jimmy.log.CoreLog;

public class CoreFragment extends Fragment {

    private HttpService httpService;
    private ApiService apiService;

    private static final String TAG = CoreFragment.class.getSimpleName();

    public void startActivity(String urlSchema) {
        this.startActivity(urlSchema, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }


    public void startActivity(String urlSchema, int pushStyle) {
        if (TextUtils.isEmpty(urlSchema)) {
            CoreLog.e("startActivity java.lang.Null: URL to null ");
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), pushStyle);
    }

    public void startActivity(Intent intent, int pushStyle) {
        super.startActivity(intent);
        setPushAnimationStyle(pushStyle);
    }

    @Override
    public void startActivity(Intent intent) {
        if (isAdded()) {
            startActivity(intent, ActivityAnimationStyle.STYLE_SLIDE_IN);
        } else {
            CoreLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ");
        }
    }

    /**
     * Activity Animation Style
     */
    private void setPushAnimationStyle(int pushStyle) {
        switch (pushStyle) {
            case ActivityAnimationStyle.STYLE_SLIDE_OUT:
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_OUT:
                getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                break;
            case ActivityAnimationStyle.STYLE_SLIDE_IN:
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_IN:
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_IN:
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_OUT:
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivityForResult(Intent intent, int requestCode, int pushStyle) {
        if (isAdded()) {
            if (intent == null) {
                return;
            }
            super.startActivityForResult(intent, requestCode);
            setPushAnimationStyle(pushStyle);
        } else {
            CoreLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ");
        }
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

}