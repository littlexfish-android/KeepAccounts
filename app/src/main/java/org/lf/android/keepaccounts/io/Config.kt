package org.lf.android.keepaccounts.io

import com.google.gson.*
import com.google.gson.stream.JsonWriter
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.StringBuilder

object Config {
	
	val defaultTag = arrayListOf("食物", "交通", "娛樂", "通常開銷")
	
	private lateinit var config: File
	private lateinit var root: JsonObject
	
	fun setConfigFile(path: String) {
		if(config != null) {
			config = File(path)
			root = JsonStreamParser(FileReader(config)).next().asJsonObject
		}
	}
	
	/**
	 * Acceptable:<br>
	 * JsonElement, JsonObject, JsonArray, JsonNull,
	 * Int, Long, Float, Double, String, Boolean.<br>
	 * or null not in above
	 */
	fun <T> getConfig(key: String, type: Class<T>): T? {
		checkConfigNotNull()
		return when(type) {
			JsonElement::class.java -> root.get(key) as T
			JsonObject::class.java -> root.get(key) as T
			JsonArray::class.java -> root.get(key) as T
			JsonNull::class.java -> root.get(key) as T
			Int::class.java -> root.get(key).asInt as T
			Long::class.java -> root.get(key).asLong as T
			Float::class.java -> root.get(key).asFloat as T
			Double::class.java -> root.get(key).asDouble as T
			String::class.java -> root.get(key).asString as T
			Boolean::class.java -> root.get(key).asBoolean as T
			else -> null
		}
	}
	
	/**
	 * Set config with any thing
	 */
	fun setConfig(key: String, value: Any?) {
		checkConfigNotNull()
		root.remove(key)
		when(value) {
			is Number -> {
				root.addProperty(key, value)
			}
			is Boolean -> {
				root.addProperty(key, value)
			}
			is String -> {
				root.addProperty(key, value)
			}
			is Char -> {
				root.addProperty(key, value)
			}
			is JsonElement -> {
				root.add(key, value)
			}
			else -> {
				root.addProperty(key, value.toString())
			}
		}
		writeToFile()
	}
	
	private fun writeToFile() {
		Gson().newBuilder().setPrettyPrinting().create().toJson(root, BufferedWriter(FileWriter(config)))
	}
	
	private fun checkConfigNotNull(): Boolean {
		if(config == null || root == null) {
			throw IllegalStateException("Config File Not Initialize")
		}
		return true
	}
	
	private fun checkAndCreateConfig(path: String): File {
		val file = File(path)
		if(!file.exists()){
			file.createNewFile()
			val jsp = JsonStreamParser(getDefaultConfigJson()).next()
			Gson().newBuilder().setPrettyPrinting().create().toJson(jsp, JsonWriter(FileWriter(file)))
		}
		return file
	}
	
	private fun getDefaultConfigJson(): String {
		val sb = StringBuilder(100)
		sb.append("{")
		//tag
		sb.append("\"customTag\": []")
		//
		
		
		sb.append("}")
		return sb.toString()
	}
	
}