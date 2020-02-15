# 使用方式

## Gradle

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
 allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
	
### Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.Jimmuy:platform-core:v1.1.0'
	}
```
## Marven
### Step 1. Add the JitPack repository to your build file
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://www.jitpack.io</url>
		</repository>
	</repositories>
```
### Step 2. Add the dependency
```
  <dependency>
	    <groupId>com.github.Jimmuy</groupId>
	    <artifactId>platform-core</artifactId>
	    <version>v1.1.0</version>
	</dependency>
```

## 使用

### 基类CoreActivity的使用

项目默认使用databinding，来减少模板代码


```<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">
    //这里自定义你想要的binding名字
    <data class="HomeActivityBinding">

        <variable
            name="OnClick"
            type="android.view.View.OnClickListener" />
    </data>


    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".HomeActivity">

        ···

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```


```
//泛型填写 xml 命名的binding文件名字，如果没有重命名则直接为当前Activity的名字+Binding,只需要填入泛型，就会初始化好bing对象，使用则是binding.xxx来获取组件对象。
class HomeActivity : CoreActivity<HomeActivityBinding>(), View.OnClickListener {

    //onCreate()中需要做的初始化可以放在这里
    override fun initView() {
        //这里进行初始化
        binding.onClick = this
        initTitle()
    }
    //实现抽象方法，将activity 的xml文件填入，父类进行setContentView
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

    //提供通用的标题和返回样式，可以自定义左边返回样式和右边操作按钮样式，如果需要自定义titlebar 可以禁用本身自带的再去自定义
    private fun initTitle() {
        
        //设置title，如果不需要title也可以重写isShowTitleBar()方法让现实全屏，并配合ImmersionBar自定义状态栏是否显示
        title = "Home"
        //设置右边副标题
        setRightText("right")
        //设置右边副标题的点击事件
        getRightView()?.let { it.setOnClickListener { showToast("right") } }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_request -> sendRequest()

            R.id.btn_debug -> initDebug()
        }
    }
    //开启调试模式，可以快速切换环境，手机端快速查看崩溃信息，设置网络随机数等等操作
    private fun initDebug() {
        requestPermission()
        DebugManager.getInstance().setDebugMode(true)
    }

    //简单调用网络请求
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
    //使用完后记得关闭debug小球，可以放在application中，随app启动开启，app关闭关闭 
    override fun onDestroy() {
        super.onDestroy()
        DebugManager.getInstance().setDebugMode(false)
    }

}


```
### 基类CoreFragment的使用
基本和CoreActivity相同