package com.jimmy.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.jimmy.log.SmartLogger


/**
 * Application with life cycle manager.
 */

abstract class CoreApplication : MultiDexApplication() {
	companion object {
		@SuppressLint("StaticFieldLeak")
		private var instance: CoreApplication? = null
		
		@JvmStatic
		fun get(): CoreApplication {
			checkNotNull(instance) { "Application has not been created" }
			return instance !!
		}
		
	}
	
	var currentActivity: Activity? = null
	private var liveCounter: Int = 0
	private var activeCounter: Int = 0
	
	/**
	 * Your SHOULDN'T NEVER call this method yourself!
	 */
	
	
	override fun onCreate() {
		super.onCreate()
		instance = this
		init()
	}
	
	/**
	 * init config as you want
	 */
	
	abstract fun init()
	
	fun onApplicationStart() {
		
		SmartLogger.i("BaseApplication::onApplicationStart")
	}
	
	fun onApplicationResume() {
		SmartLogger.i("BaseApplication::onApplicationResume")
	}
	
	fun onApplicationPause() {
		SmartLogger.i("BaseApplication::onApplicationPause")
		
	}
	
	fun onApplicationStop() {
		SmartLogger.i("BaseApplication::onApplicationStop")
		
	}
	
	
	fun activityOnCreate(a: Activity) {
		if (liveCounter ++ == 0) {
			onApplicationStart()
		}
	}
	
	fun activityOnResume(a: Activity) {
		if (activeCounter ++ == 0) {
			onApplicationResume()
		}
	}
	
	fun activityOnPause(a: Activity) {
		handler.sendEmptyMessageDelayed(1, 100)
	}
	
	fun activityOnDestroy(a: Activity) {
		if (-- liveCounter == 0) {
			onApplicationStop()
		}
	}
	
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}
	
	
	private val handler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message) {
			if (msg.what == 1) {
				if (-- activeCounter == 0) {
					onApplicationPause()
				}
			}
		}
	}
}
