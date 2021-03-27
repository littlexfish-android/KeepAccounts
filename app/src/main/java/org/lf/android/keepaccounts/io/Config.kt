package org.lf.android.keepaccounts.io

import com.google.gson.*
import com.google.gson.stream.JsonWriter
import java.io.*
import java.lang.StringBuilder

object Config {
	
	
	private const val writeInterval = 1000
	var lastWriteConfig = System.currentTimeMillis()
	
	private lateinit var config: File
	private lateinit var root: JsonObject
	private lateinit var element: JsonElement
	
	fun setConfigFile(path: File) {
		if(!::config.isInitialized) {
			config = checkAndCreateConfig(path)
			element = JsonStreamParser(FileReader(config)).next()
			root = element.asJsonObject
		}
	}
	
	/**
	 * Acceptable:<br>
	 * JsonElement, JsonObject, JsonArray, JsonNull,
	 * Int, Long, Float, Double, String, Boolean.<br>
	 * or throw IllegalArgumentException not in above
	 */
	fun <T> getConfig(key: String, type: Class<T>): T {
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
			else -> throw IllegalArgumentException("type error")
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
		if((System.currentTimeMillis() - lastWriteConfig) > writeInterval) {
			writeToFile()
		}
	}
	
	fun forceWrite() {
		writeToFile()
	}
	
	private fun writeToFile() {
		lastWriteConfig = System.currentTimeMillis()
		val ele = Gson().newBuilder().setPrettyPrinting().create().toJson(root)
		write(config, ele)
	}
	
	private fun checkConfigNotNull(): Boolean {
		if(!::config.isInitialized || !::root.isInitialized || !::element.isInitialized) {
			throw IllegalStateException("Config File Not Initialize")
		}
		return true
	}
	
	private fun checkAndCreateConfig(path: File): File {
		val file = File(path.absolutePath + "/config.json")
		if(!file.exists()) {
			file.createNewFile()
			val jsp = JsonStreamParser(getDefaultConfigJson()).next()
			val ele = Gson().newBuilder().setPrettyPrinting().create().toJson(jsp)
			write(file, ele)
		}
		return file
	}
	
	private fun getDefaultConfigJson(): String {
		val sb = StringBuilder(100)
		sb.append("{")
		//tag
		sb.append("\"customTag\": [], ")
		//sync
		sb.append("\"lastSync\": 0, ")
		sb.append("\"autoSync\": false, ")
		//total & detail
		sb.append("\"lastRecord\": 0, ")
		sb.append("\"total\": 0, ")
		sb.append("\"hasDetail\": true, ")
		sb.append("\"detail\": {\"twoThousand\": 0, \"thousand\": 0, \"fiveHundred\": 0, \"twoHundred\": 0, \"hundred\": 0, \"fifty\": 0, \"twenty\": 0, \"ten\": 0, \"five\": 0, \"one\": 0}")
		
		sb.append("}")
		return sb.toString()
	}
	
	private fun write(file: File, content: String) {
		val fos = FileOutputStream(file)
		for(char in content) {
			fos.write(char.toInt())
		}
		fos.close()
	}
	
}