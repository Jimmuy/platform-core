package com.qcec.app;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcec.core.R;

public class TitleBar {

    private Activity mActivity;

    public static final int CUSTOM_TITLE = 1;
    public static final int NO_TITLE = 2;

    private int style;

    private View rootView;
    private ViewGroup leftViewContainer;
    private ViewGroup contentViewContainer;
    private ViewGroup rightViewContainer;

    private ImageView leftView; 

    private TextView titleView;
    private TextView subTitleView;

    public TitleBar(Activity activity, int style) {
        this.mActivity = activity;
        this.style = style;
        setupTitleBar();
    }

    private void setupTitleBar() {
        switch (style) {
            case CUSTOM_TITLE:
                initCustomTitle();
                break;
            case NO_TITLE:
                initNoTitle();
                break;
            default:
                break;
        }

    }  

    private void initCustomTitle() {
        mActivity.getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
        mActivity.setContentView(new ViewStub(mActivity));
        mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.default_title_bar);
        rootView = mActivity.findViewById(R.id.title_bar);
        leftViewContainer = (ViewGroup) rootView.findViewById(R.id.title_bar_left_view_container);
        leftView = (ImageView) leftViewContainer.findViewById(R.id.left_view);
        contentViewContainer = (ViewGroup) rootView.findViewById(R.id.title_bar_content_container);
        titleView = (TextView) contentViewContainer.findViewById(R.id.title_bar_title);
        subTitleView = (TextView) contentViewContainer.findViewById(R.id.title_bar_subtitle);
        rightViewContainer = (ViewGroup) rootView.findViewById(R.id.title_bar_right_view_container);
    }

    private void initNoTitle() {
        mActivity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    public int getTitleStyle() {
        return style;
    }

    /**
     * 自定义标题栏背景
     */
    public void setBackground(int resId) {
        if (style == NO_TITLE) {
            throw new RuntimeException("Current title bar style is NO TITLE");
        }
        rootView.setBackgroundResource(resId);
    }

    /**
     * 自定义标题栏背景
     */
    public void setBackground(Drawable drawable) {
        rootView.setBackgroundDrawable(drawable);
    }

    /**
     * 设置默认标题栏左边按钮点击事件
     */
    public void setLeftView(View.OnClickListener l) {
        setLeftView(0, l);
    }

    /**
     * 设置默认标题栏左边按钮res及点击事件
     */
    public void setLeftView(int resId, View.OnClickListener l) {
        if (resId == -1) {
            leftView.setVisibility(View.GONE);
        } else if (resId == 0) {
            leftView.setImageResource(R.drawable.back);
            leftView.setVisibility(View.VISIBLE);
        } else if (resId > 0) {
            leftView.setImageResource(resId);
            leftView.setVisibility(View.VISIBLE);
        }

        leftView.setOnClickListener(l);
        leftViewContainer.setOnClickListener(l);

        if(leftViewContainer.indexOfChild(leftView) < 0) {
            leftViewContainer.removeAllViews();
            leftViewContainer.addView(leftView);
        }
    }

    /**
     * 自定义标题栏左边按钮
     */
    public void setCustomLeftView(int resId, View.OnClickListener l) {
        View customView = LayoutInflater.from(mActivity).inflate(resId, leftViewContainer, false);
        setCustomLeftView(customView, l);
    }

    /**
     * 自定义标题栏左边按钮
     */
    public void setCustomLeftView(View view, View.OnClickListener l) {
        leftViewContainer.removeAllViews();
        leftViewContainer.addView(view);

        leftViewContainer.setOnClickListener(l);
    }

    /**
     * 自定义标题栏中间视图
     */
    public void setCustomContentView(View view) {
        contentViewContainer.removeAllViews();
        contentViewContainer.addView(view);
    }

    /**
     * 增加标题栏右边自定义按钮
     */
    public void addRightViewItem(String tag, int drawableId, View.OnClickListener l) {
        Drawable drawable = mActivity.getResources().getDrawable(drawableId);
        addRightViewItem(tag, drawable, l);
    }
    
    /**
    * 增加标题栏右边自定义按钮
    */
   public void addRightViewItem(String tag, String text, View.OnClickListener l) {
	   TextView textView = new TextView(mActivity);
       final int margin = mActivity.getResources().getDimensionPixelSize(R.dimen.titlebar_right_margin);
       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
               ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       layoutParams.setMargins(margin, 0, 0, 0);
       textView.setText(text);
       textView.setTextColor(mActivity.getResources().getColor(R.color.white));
       textView.setTextSize(15);
       textView.setBackgroundResource(android.R.color.transparent);
       textView.setLayoutParams(layoutParams);
       addRightViewItem(tag, textView, l);
   }

    /**
     * 增加标题栏右边自定义按钮
     */
    public void addRightViewItem(String tag, Drawable drawable, View.OnClickListener l) {
        ImageView imageView = new ImageView(mActivity);
        final int imageViewMargin = mActivity.getResources()
                .getDimensionPixelSize(R.dimen.titlebar_right_margin);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(imageViewMargin, 0, 0, 0);
        imageView.setBackgroundResource(android.R.color.transparent);
        imageView.setImageDrawable(drawable);
        imageView.setLayoutParams(layoutParams);
        addRightViewItem(tag, imageView, l);
    }

    /**
     * 增加标题栏右边自定义按钮
     */
    public void addRightViewItem(String tag, View view, View.OnClickListener l) {
        view.setOnClickListener(l);
        if (!TextUtils.isEmpty(tag)) {
            view.setTag(R.id.title_bar_right_view_container, tag);
        }
        View child = findViewByTag(tag);
        if (child != null) {
            rightViewContainer.removeView(child);
            rightViewContainer.addView(view);
        } else {
            rightViewContainer.addView(view);
        }
    }

    /**
     * 删除标题栏右侧指定Tag的按钮
     */
    public void removeRightViewItem(String tag) {
        View view = findViewByTag(tag);
        if (view != null) {
            rightViewContainer.removeView(view);
        }
    }

    /**
     * 删除所有标题栏右边自定义按钮
     */
    public void removeAllRightViewItem() {
        rightViewContainer.removeAllViews();
    }

    /**
     * 根据Tag查找标题栏右边对应的child
     */
    public View findViewByTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }

        for (int i = 0; i < rightViewContainer.getChildCount(); i++) {
            View childView = rightViewContainer.getChildAt(i);
            if (tag.equals(childView.getTag(R.id.title_bar_right_view_container))) {
                return childView;
            }
        }
        return null;
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public void setSubTitle(CharSequence subTitle) {
        subTitleView.setText(subTitle);
    }

    public void show() {
        rootView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        rootView.setVisibility(View.GONE);
    }

}
