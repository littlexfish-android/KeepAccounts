package org.lf.android.keepaccounts.io

import com.google.gson.JsonArray
import com.google.gson.JsonStreamParser
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

object HistoryFunc {
	
	fun getFileFromYearMonth(parent: File, year: Int, month: Int): JsonArray {
		val file = File(parent.absolutePath + "/$year/$month.json")
		if(!file.exists()) {
			Logger.e("historyFile", "file not found: ${file.absolutePath}")
			throw FileNotFoundException(file.absolutePath)
		}
		return JsonStreamParser(FileReader(file)).next().asJsonArray
	}
	
	fun getFileShowName(parent: File): Array<String> {
		val list = ArrayList<String>(10)
		val dirList = parent.listFiles()
		for(dir in dirList) {
			val files = dir.listFiles()
			for(file in files) {
				list.add("${dir.name}/${file.name.substring(0, file.name.length - 5)}")
			}
		}
		return list.toArray(arrayOf(""))
	}
	
}