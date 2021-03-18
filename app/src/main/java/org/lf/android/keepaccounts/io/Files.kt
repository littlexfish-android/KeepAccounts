package org.lf.android.keepaccounts.io

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets

class Files(filesDir: File) {
	
	private var rootDataDir = File(filesDir.absolutePath + "/Data")
	private val ref = FirebaseStorage.getInstance().reference.child("Data")

	init {
		checkAndCreateRootDir()
	}

	fun saveData(data: DataHandler, year: Int, month: Int, day: Int, isPay: Boolean = true) {
		Logger.d("file", "Start create ")
		val file = checkAndCreateMonthFile(year, month)
		val sb = StringBuilder(file.length().toInt() + 256)
		sb.append("{").append("\"timeStamp\": ${data.getTimeStamp()}, ")
		sb.append("\"type\": \"").append(when(isPay) {true -> "pay";false -> "income"}).append("\", ")
		sb.append("\"date\": \"$year/$month/$day\", ")
		sb.append("\"tag\": ${data.getTagString()}, ")
		sb.append("\"remark\": \"${data.getRemark()}\", ")
		sb.append("\"total\": ${data.data.count()}, ")
		sb.append("\"detail\": {").append("\"twoThousand\": ${data.data.twoThousand}, ")
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
		writeData(year, file, JsonStreamParser(sb.toString()).next())
	}

	fun deleteData(dir: Int, file: File, timeStamp: Long) {
		var toDelete: JsonObject? = null
		val jsp = JsonStreamParser(FileReader(file)).next().asJsonArray
		for(jo in jsp) {
			var tmp = jo.asJsonObject
			if(tmp.get("timeStamp").asLong == timeStamp) {
				toDelete = tmp
			}
		}
		if(toDelete != null) {
			jsp.remove(toDelete)
			writeData(dir, file, jsp)
		}
	}

	private fun writeData(dir: Int, file: File, data: JsonElement) {
		val jsp = JsonStreamParser(FileReader(file)).next().asJsonArray
		jsp.add(data)
		val fos = FileOutputStream(file)
		val content = StandardCharsets.ISO_8859_1.decode(StandardCharsets.UTF_8.encode(Gson().newBuilder().setPrettyPrinting().create().toJson(jsp))).toString()
		for(char in content) {
			fos.write(char.toInt())
		}
		fos.close()
		uploadFile(dir, file)
	}

	private fun checkAndCreateMonthFile(year: Int, month: Int): File {
		val monthFile = File(checkAndCreateYearDir(year).absolutePath + "/$month.json")
		if(!monthFile.exists()) {
			monthFile.createNewFile()
			val jsp = JsonStreamParser("[]").next()
			val fos = FileOutputStream(monthFile)
			val content = jsp.toString()
			for(char in content) {
				fos.write(char.toInt())
			}
			fos.close()
		}
		return monthFile
	}
	
	private fun checkAndCreateYearDir(year: Int): File {
		val yearDir = File(rootDataDir.absolutePath + "/$year")
		if(!yearDir.exists()) {
			yearDir.mkdir()
		}
		return yearDir
	}

	private fun checkAndCreateRootDir() {
		if(!rootDataDir.exists()) {
			rootDataDir.mkdir()
		}
	}

	private fun uploadFile(dir: Int, file: File) {
		val uploadRef = ref.child("$dir/${file.name}").putFile(file.toUri()).addOnSuccessListener {
			Logger.i("uploadFile", "upload: ${file.absolutePath} to ${it.storage.path}")
		}.addOnFailureListener {
			Logger.e("uploadFile", "uploadError on: \n${it.stackTrace}")
		}
	}

}