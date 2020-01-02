package com.jimmy.app

interface ILoadingDialog {
	fun showLoading()
	fun dismissLoading()
	fun isShowing(): Boolean
}
