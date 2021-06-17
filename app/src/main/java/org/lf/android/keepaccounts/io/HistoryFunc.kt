package org.lf.android.keepaccounts.io

import com.google.gson.JsonArray
import com.google.gson.JsonStreamParser
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

object HistoryFunc {

	fun getFileFromYearMonth(parent: File, year: Int, month: Int): JsonArray {
		val file = File(parent.absolutePath + "/Data/$year/$month.json")
		if(!file.exists()) {
			Logger.e("historyFile", "file not found: ${file.absolutePath}")
			throw FileNotFoundException(file.absolutePath)
		}
		return JsonStreamParser(FileReader(file)).next().asJsonArray
	}
	
	fun getFileShowName(parent: File): Array<String> {
		val list = ArrayList<String>(10)
		val dirList = File("${parent.absolutePath}/Data").listFiles()
		if(dirList != null) {
			for(dir in dirList) {
				val files = dir.listFiles()
				if(files != null) {
					for(file in files) {
						list.add("${dir.name}/${file.name.substring(0, file.name.length - 5)}")
					}
				}
			}
		}
		return list.toArray(arrayOf(""))
	}

	fun getPercentage(parent: File, type: Category, param: String, stat: String): FloatArray {
		val list = ArrayList<Float>()
		val dirList = File("${parent.absolutePath}/Data").listFiles()
		when(type) {
			Category.Month -> {
				when(stat) {
					"支出入" -> {
						var `in` = 0
						var out = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											if(je.asJsonObject.get("date").asString.startsWith(param)) {
												if(je.asJsonObject.get("type").asString == "income") {
													`in`++
												}
												else {
													out++
												}
											}
										}
									}
								}
							}
						}
						val sum = (`in` + out).toFloat()
						list.add(`in` / sum)
						list.add(out / sum)

					}
					else -> {
						var food = 0
						var trans = 0
						var play = 0
						var usually = 0
						var other = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											if(je.asJsonObject.get("date").asString.startsWith(param)) {
												when (je.asJsonObject.get("tag").asString) {
													"食物" -> food++
													"交通" -> trans++
													"娛樂" -> play++
													"通常開銷" -> usually++
													else -> other++
												}
											}
										}
									}
								}
							}
						}
						val sum = (food + trans + play + usually + other).toFloat()
						list.add(food / sum)
						list.add(trans / sum)
						list.add(play / sum)
						list.add(usually / sum)

					}
				}
			}
			Category.Year -> {
				when(stat) {
					"支出入" -> {
						var `in` = 0
						var out = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											if(je.asJsonObject.get("type").asString == "income") {
												`in`++
											}
											else {
												out++
											}
										}
									}
								}
							}
						}
						val sum = (`in` + out).toFloat()
						list.add(`in` / sum)
						list.add(out / sum)

					}
					else -> {
						var food = 0
						var trans = 0
						var play = 0
						var usually = 0
						var other = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											when (je.asJsonObject.get("tag").asString) {
												"食物" -> food++
												"交通" -> trans++
												"娛樂" -> play++
												"通常開銷" -> usually++
												else -> other++
											}
										}
									}
								}
							}
						}
						val sum = (food + trans + play + usually + other).toFloat()
						list.add(food / sum)
						list.add(trans / sum)
						list.add(play / sum)
						list.add(usually / sum)

					}
				}
			}
			Category.All -> {
				when(stat) {
					"支出入" -> {
						var `in` = 0
						var out = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											if(je.asJsonObject.get("date").asString.startsWith(param)) {
												if(je.asJsonObject.get("type").asString == "income") {
													`in`++
												}
												else {
													out++
												}
											}
										}
									}
								}
							}
						}
						val sum = (`in` + out).toFloat()
						list.add(`in` / sum)
						list.add(out / sum)

					}
					else -> {
						var food = 0
						var trans = 0
						var play = 0
						var usually = 0
						var other = 0
						if(dirList != null) {
							for(dir in dirList) {
								val files = dir.listFiles()
								if(files != null) {
									for(file in files) {
										val json = JsonStreamParser(FileReader(file)).next().asJsonArray
										for(je in json) {
											if(je.asJsonObject.get("date").asString.startsWith(param)) {
												when (je.asJsonObject.get("tag").asString) {
													"食物" -> food++
													"交通" -> trans++
													"娛樂" -> play++
													"通常開銷" -> usually++
													else -> other++
												}
											}
										}
									}
								}
							}
						}
						val sum = (food + trans + play + usually + other).toFloat()
						list.add(food / sum)
						list.add(trans / sum)
						list.add(play / sum)
						list.add(usually / sum)

					}
				}
			}
		}

		val fa = FloatArray(list.size)
		for((i, v) in list.withIndex()) {
			fa[i] = v
		}
		return fa
	}


	enum class Category(val showName: String) {
		Month("月份"), Year("年份"), All("所有紀錄")
	}
	
}