package com.jimmy.dataSource

import android.widget.Toast
import com.jimmy.app.CoreApplication

/*数据处理中心，不要在方法中做耗时操作*/
abstract class DataHandler<T> {
	open fun onLoadDataStart(it: Resource<T>) {
	
	}
	
	open fun onLoadDataEmpty(it: Resource<T>) {
	
	}
	
	open fun onLoadDataError(it: Resource<T>) {
		it.message?.let {
			Toast.makeText(CoreApplication.get(), it, Toast.LENGTH_SHORT).show()
		}
	}
	
	abstract fun onLoadDataSuccess(it: Resource<T>)
}