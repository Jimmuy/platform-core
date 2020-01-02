package com.jimmy.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast

import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ImmersionBar
import com.jimmy.core.R
import com.jimmy.core.databinding.TitleBinding
import com.jimmy.dataSource.DataHandler
import com.jimmy.dataSource.Resource
import com.jimmy.dataSource.Status
import com.jimmy.utils.dpToPixel

/**
 * Base Activity extends fragment activity.
 * Contains basic service and widget.
 * T is Data Binding class name
 */
abstract class CoreActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: T
    private var titleBinding: TitleBinding? = null

    private lateinit var progressDialog: ILoadingDialog

    /**
     * 默认显示title bar 子类可以复写去除title bar
     */
    protected open fun isShowTitleBar(): Boolean = true


    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    protected open fun getHeaderTitle(): TextView? = titleBinding?.tvTitle

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    protected open fun getRightView(): TextView? = titleBinding?.tvRightText

    /**
     * 设置左边返回按钮图片资源
     */
    protected open fun getLeftDrawable(): Int = R.drawable.back


    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    protected open fun isShowBacking(): Boolean = true

    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     *
     * @return res layout xml id
     */
    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        CoreApplication.get().activityOnCreate(this)
        initStatusBar()
        initToolBar()
        initView()
    }

    protected open fun initStatusBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarDarkFont(true, 0.2f)
            .statusBarColor(R.color.white).init()
    }

    private fun initToolBar() {

        if (isShowTitleBar()) {
            require(binding.root is ViewGroup) { "root view must be a view group" }
            titleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.view_toolbar,
                null,
                false
            )
            titleBinding?.root?.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPixel(this, 44f))
            (binding.root as ViewGroup).addView(titleBinding?.root, 0)

            if (titleBinding == null) return
            //将Toolbar显示到界面
            setSupportActionBar(titleBinding?.toolbar)
            //设置默认的标题不显示
            supportActionBar?.setDisplayShowTitleEnabled(false)

            //getTitle()的值是activity的android:lable属性值
            titleBinding?.tvTitle?.text = title
            if (isShowBacking()) {
                showBack()
            }

        }

    }

    /**
     * 子类可复写做一些初始化的工作
     */
    protected open fun initView() {
        progressDialog = getProgressDialog()
    }
    
    abstract fun getProgressDialog(): ILoadingDialog

    override fun onResume() {
        super.onResume()
        CoreApplication.get().activityOnResume(this)
        CoreApplication.get().currentActivity = this
        /**
         * 判断是否有Toolbar,并默认显示返回按钮
         */
        if (null != titleBinding && isShowBacking()) {
            showBack()
        }
    }


    override fun onPause() {
        super.onPause()
        CoreApplication.get().activityOnPause(this)
        if (this == CoreApplication.get().currentActivity) {
            CoreApplication.get().currentActivity = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoreApplication.get().activityOnDestroy(this)
        if (this == CoreApplication.get().currentActivity) {
            CoreApplication.get().currentActivity = null
        }
        progressDialog?.dismissLoading()
        binding.unbind()


    }

    fun setRightText(rightTitle: CharSequence) {
        getRightView()?.visibility = View.VISIBLE
        getRightView()?.text = rightTitle

    }

    override fun setTitleColor(color: Int) {
        titleBinding?.tvTitle?.setTextColor(color)
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    override fun setTitle(title: CharSequence) {
        if (titleBinding?.tvTitle != null) {
            titleBinding?.tvTitle?.text = title
        }
    }


    /**
     * 提供简单的返回按钮
     */
    private fun showBack() {
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        titleBinding?.toolbar?.setNavigationIcon(getLeftDrawable())
        titleBinding?.toolbar?.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * 设置title的背景颜色
     *
     * @return
     */
    fun setToolBarBackground(@ColorInt color: Int) {
        titleBinding?.toolbar?.setBackgroundColor(color)
    }

    /**
     * 关闭软键盘
     *
     * @param view
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 开启软键盘
     */
    fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun showToast(msg: String?) {
        msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }

    /*统一设置数据请求界面loading展示.上层也可重写此方法进行自定义loading加载*/
    protected fun <T> setStates(
        it: Resource<T>,
        listener: DataHandler<T>,
        needLoading: Boolean = true
    ) {
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


    fun hideLoadingDialog() {
        progressDialog.dismissLoading()

    }

    fun showLoadingDialog() {
   
        if (!progressDialog.isShowing() && !isFinishing) {
            progressDialog.showLoading()
        }
    }

}
