package com.jimmy.sample

import android.accounts.AccountManager
import android.util.Log
import com.jimmy.app.CoreApplication
import com.jimmy.debug.DebugManager
import com.jimmy.iot.net.HttpClient
import com.jimmy.iot.net.intercepter.HttpLoggingInterceptor
import com.jimmy.iot.net.model.HttpHeaders
import com.jimmy.iot.net.model.HttpParams
import com.jimmy.sample.net.DomainConfig
import java.util.*

class BaseApplication : CoreApplication() {
	override fun init() {
		DebugManager.getInstance().setDebugActivity(HomeActivity::class.java)
		initNetConfig()
	}
	private fun initNetConfig() {
		HttpClient.init(this)//默认初始化,必须调用
		//全局设置请求头
		val headers = HttpHeaders()
		headers.put("Content-Type", "application/json")

		//全局设置请求参数
		val params = HttpParams()
		

		
		//以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
		HttpClient.getInstance()
				
				//可以全局统一设置全局URL
				.setBaseUrl(DomainConfig.apiDomain.url)//设置全局URL  url只能是域名 或者域名+端口号
				
				// 打开该调试开关并设置TAG,不需要就不要加入该行
				// 最后的true表示是否打印内部异常，一般打开方便调试错误
				//                .debug("HttpClient", true)
				//不需要可以设置为0
				.setRetryCount(3)//网络不好自动重试3次
				//可以全局统一设置超时重试间隔时间,默认为500ms,不需要可以设置为0
				.setRetryDelay(500)//每次延时500ms重试
				//可以全局统一设置超时重试间隔叠加时间,默认为0ms不叠加
				.setRetryIncreaseDelay(500)//每次延时叠加500ms
				//可以设置https的证书,以下几种方案根据需要自己设置
				
				.addCommonHeaders(headers)//设置全局公共头
				.addCommonParams(params)

	}
}