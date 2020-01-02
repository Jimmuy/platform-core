package com.jimmy.app

import android.app.Dialog
import android.content.Context
import com.jimmy.core.R

/**
 * loading
 */
class CoreLoadingDialog(context: Context) : Dialog(context), ILoadingDialog {
	override fun showLoading() {
		show()
	}

	override fun dismissLoading() {
		if (isShowing) dismiss()
	}

	init {
		setCanceledOnTouchOutside(false)
		setContentView(R.layout.dialog_loading)
	}
}