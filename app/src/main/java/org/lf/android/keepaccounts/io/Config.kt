package org.lf.android.keepaccounts.io

import com.google.gson.*
import com.google.gson.stream.JsonWriter
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.StringBuilder

object Config {
	
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
			JsonElement::javaClass -> root.get(key) as T
			JsonObject::javaClass -> root.get(key) as T
			JsonArray::javaClass -> root.get(key) as T
			JsonNull::javaClass -> root.get(key) as T
			Int::javaClass -> root.get(key).asInt as T
			Long::javaClass -> root.get(key).asLong as T
			Float::javaClass -> root.get(key).asFloat as T
			Double::javaClass -> root.get(key).asDouble as T
			String::javaClass -> root.get(key).asString as T
			Boolean::javaClass -> root.get(key).asBoolean as T
			else -> null
		}
	}
	
	/**
	 * Set config with any thing
	 */
	fun setConfig(key: String, value: Any?) {
		checkConfigNotNull()
		root.remove(key)
		if(value is Number) {
			root.addProperty(key, value)
		}
		else if(value is Boolean) {
			root.addProperty(key, value)
		}
		else if(value is String) {
			root.addProperty(key, value)
		}
		else if(value is Char) {
			root.addProperty(key, value)
		}
		else if(value is JsonElement) {
			root.add(key, value)
		}
		else {
			root.addProperty(key, value.toString())
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
		sb.append("\"tag\": []")
		
		
		
		sb.append("}")
		return sb.toString()
	}
	
}