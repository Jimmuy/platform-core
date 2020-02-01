package com.jimmy.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionFragment

import com.jimmy.core.R
import com.jimmy.core.databinding.TitleBinding
import com.jimmy.dataSource.DataHandler
import com.jimmy.dataSource.Resource
import com.jimmy.dataSource.Status
import com.jimmy.log.CoreLog
import com.jimmy.utils.ActivityAnimationStyle
import com.jimmy.utils.dpToPixel

abstract class CoreFragment<T : ViewDataBinding> : SimpleImmersionFragment() {

    private lateinit var progressDialog: ILoadingDialog
    protected lateinit var binding: T
    private lateinit var titleBinding: TitleBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initToolBar()
        initView()
        return binding.root
    }

    protected open fun initView() {
        progressDialog = (activity as CoreActivity<*>).setupProgressDialog()
    }


    private fun initToolBar() {
        if (isShowTitleBar()) {
            require(binding.root is ViewGroup) { "root view must be a view group" }
            titleBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.view_toolbar, null, false)
            titleBinding.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPixel(activity, getTitleBarHeight()))
            (binding.root as ViewGroup).addView(titleBinding.root, 0)


            //getTitle()的值是activity的android:lable属性值
            titleBinding.tvTitle.text = activity?.title
            if (isShowBacking()) {
                showBack()
            }

        }

    }

    abstract fun getLayoutId(): Int
    /**
     * 默认显示title bar 子类可以复写去除title bar
     */
    protected open fun isShowTitleBar(): Boolean {
        return false
    }

    /**
     * 子类可复写来设置默认title bar 高度
     */
    protected open fun getTitleBarHeight() = DEFAULT_TITLE_BAR_HEIGHT

    fun showToast(msg: String) {
        Toast.makeText(CoreApplication.get(), msg, Toast.LENGTH_SHORT).show()
    }


    override fun initImmersionBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarDarkFont(true, 0.2f).statusBarColor(R.color.white).init()
    }

    open fun startActivity(urlSchema: String?) {
        this.startActivity(urlSchema, ActivityAnimationStyle.STYLE_SLIDE_IN)
    }


    open fun startActivity(urlSchema: String?, pushStyle: Int) {
        if (TextUtils.isEmpty(urlSchema)) {
            CoreLog.e("startActivity java.lang.Null: URL to null ")
            return
        }
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), pushStyle)
    }

    open fun startActivity(intent: Intent?, pushStyle: Int) {
        super.startActivity(intent)
        setPushAnimationStyle(pushStyle)
    }

    override fun startActivity(intent: Intent?) {
        if (isAdded) {
            startActivity(intent, ActivityAnimationStyle.STYLE_SLIDE_IN)
        } else {
            CoreLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ")
        }
    }

    /**
     * Activity Animation Style
     */
    private fun setPushAnimationStyle(pushStyle: Int) {
        when (pushStyle) {
            ActivityAnimationStyle.STYLE_SLIDE_OUT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
            }
            ActivityAnimationStyle.STYLE_MODAL_OUT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
            }
            ActivityAnimationStyle.STYLE_SLIDE_IN -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
            }
            ActivityAnimationStyle.STYLE_MODAL_IN -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
            }
            ActivityAnimationStyle.STYLE_RIGHT_SLIDE_IN -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
            }
            ActivityAnimationStyle.STYLE_RIGHT_SLIDE_OUT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                activity?.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
            }
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        startActivityForResult(intent, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN)
    }

    open fun startActivityForResult(intent: Intent?, requestCode: Int, pushStyle: Int) {
        if (isAdded) {
            if (intent == null) {
                return
            }
            super.startActivityForResult(intent, requestCode)
            setPushAnimationStyle(pushStyle)
        } else {
            CoreLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ")
        }
    }

    protected open fun hideLoadingDialog() {
        progressDialog.dismissLoading()
    }

    protected open fun showLoadingDialog() {
        if (activity == null || isDetached) {
            return
        }
        if (!progressDialog.isShowing() && !isDetached && activity != null) {
            progressDialog.showLoading()
        }
    }

    /*统一设置数据请求界面loading展示.上层也可重写此方法进行自定义loading加载*/
    protected fun <T> setStates(
            it: Resource<T>, listener: DataHandler<T>, needLoading: Boolean = true) {
        when (it.status) {
            Status.LOADING -> {
                if (needLoading) showLoadingDialog()
                listener.onLoadDataStart(it)
            }
            Status.SUCCESS -> {
                if (needLoading) hideLoadingDialog()
                listener.onLoadDataSuccess(it)
            }
            Status.ERROR -> {
                if (needLoading) hideLoadingDialog()
                listener.onLoadDataError(it)
            }
            Status.EMPTY -> listener.onLoadDataEmpty(it)
        }
    }


    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    protected open fun getHeaderTitle(): TextView {
        return titleBinding.tvTitle
    }

    protected open fun setRightText(rightTitle: CharSequence) {
        getRightView().visibility = View.VISIBLE
        getRightView().text = rightTitle

    }

    /**
     * 获取标题右侧的TextView
     *
     * @return
     */
    protected open fun getRightView(): TextView {
        return titleBinding.tvRightText
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    protected open fun setTitle(title: CharSequence) {
        titleBinding.tvTitle.text = title
    }


    /**
     * 提供简单的返回按钮
     */
    private fun showBack() {
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        titleBinding.toolbar.setNavigationIcon(getLeftDrawable())
        titleBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    protected open fun onBackPressed() {
        activity?.onBackPressed()
    }

    /**
     * 设置左边返回按钮图片资源
     */
    protected open fun getLeftDrawable(): Int {
        return R.drawable.back
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    protected open fun isShowBacking(): Boolean {
        return false
    }

    /**
     * 设置title的背景颜色
     *
     * @return
     */
    protected open fun setToolBarBackground(@ColorInt color: Int) {
        titleBinding.toolbar.setBackgroundColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismissLoading()
    }

}
