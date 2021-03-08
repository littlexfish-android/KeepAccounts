package org.lf.android.keepaccounts.io

import java.io.File

object Directory {
	
	fun checkAndCreateRoot(dir: File) {
		val rootDir = File(dir.absolutePath + "/Data")
		if(!rootDir.exists()) {
			rootDir.mkdir()
		}
	}
	
	
}