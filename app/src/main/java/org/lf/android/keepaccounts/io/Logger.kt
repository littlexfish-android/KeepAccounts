package org.lf.android.keepaccounts.io

import android.util.Log

object Logger {
	
	fun d(category: String, msg: String) = Log.d("簡易記帳-$category", msg)
	fun i(category: String, msg: String) = Log.i("簡易記帳-$category", msg)
	fun w(category: String, msg: String) = Log.w("簡易記帳-$category", msg)
	fun e(category: String, msg: String) = Log.e("簡易記帳-$category", msg)
	
}