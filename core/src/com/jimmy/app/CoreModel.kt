package com.jimmy.app

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CoreModel : Parcelable {
	/**
	 * `版本号`.
	 */
	@IgnoredOnParcel
	private var lastVer: Int? = 0
	
	fun getLastVer(): Int? {
		return lastVer
	}
	
	fun setLastVer(lastVer: Int?) {
		this.lastVer = lastVer
	}
}