package org.lf.android.keepaccounts.io

import android.util.Log
import com.google.gson.*
import com.google.gson.stream.JsonWriter
import java.io.File
import java.io.FileWriter
import java.lang.StringBuilder

class Files(filesDir: File) {
	
	private var rootDataDir = File(filesDir.absolutePath + "/Data")
	
	fun saveData(data: DataHandler, year: Int, month: Int, day: Int) {
		val file = checkAndCreateMonthFile(year, month)
		val sb = StringBuilder(file.length().toInt() + 256)
		sb.append("{").append("\"timeStamp\": ${data.getTimeStamp()}, ")
		sb.append("\"date\": \"$year$month$day\"")
		sb.append("\"tag\": [${data.getTagString()}], ")
		sb.append("\"total\": ${data.data.count()}, ")
		sb.append("\"detail: {\"").append("\"twoThousand\": ${data.data.twoThousand}, ")
		sb.append("\"thousand\": ${data.data.thousand}, ")
		sb.append("\"fiveHundred\": ${data.data.fiveHundred}, ")
		sb.append("\"twoHundred\": ${data.data.twoHundred}, ")
		sb.append("\"hundred\": ${data.data.hundred}, ")
		sb.append("\"fifty\": ${data.data.fifty}, ")
		sb.append("\"twenty\": ${data.data.twenty}, ")
		sb.append("\"ten\": ${data.data.ten}, ")
		sb.append("\"five\": ${data.data.five}, ")
		sb.append("\"one\": ${data.data.one}").append("}")
		sb.append("}")
		writeData(file, JsonStreamParser(sb.toString()).next())
	}
	
	private fun writeData(file: File, data: JsonElement) {
		Gson().newBuilder().setPrettyPrinting().create().toJson(data, JsonWriter(FileWriter(file)))
	}
	
	fun checkAndCreateMonthFile(year: Int, month: Int): File {
		val monthFile = File(checkAndCreateYearDir(year).absolutePath + "/$month.json")
		if(!monthFile.exists()) {
			monthFile.createNewFile()
			val jsp = JsonStreamParser("{}").next()
			Gson().newBuilder().setPrettyPrinting().create().toJson(jsp, JsonWriter(FileWriter(monthFile)))
		}
		return monthFile
	}
	
	fun checkAndCreateYearDir(year: Int): File {
		val yearDir = File(rootDataDir.absolutePath + "/$year")
		if(!yearDir.exists()) {
			yearDir.mkdir()
		}
		return yearDir
	}
	
}