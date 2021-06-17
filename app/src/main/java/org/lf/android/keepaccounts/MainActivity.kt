package org.lf.android.keepaccounts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.google.gson.JsonStreamParser
import org.lf.android.keepaccounts.io.Config
import org.lf.android.keepaccounts.io.Logger
import org.lf.android.keepaccounts.view.DetailView
import org.lf.android.keepaccounts.view.PieChartView
import org.lf.android.keepaccounts.view.PieView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader

class MainActivity : AppCompatActivity() {

	private val ref = FirebaseStorage.getInstance().reference.child("Data")
	private lateinit var data: File
	private lateinit var tmpDir: File
	
	private lateinit var drawerLayout: DrawerLayout
	private lateinit var optionButt: ImageView
	private lateinit var options: NavigationView
	
	private lateinit var money: TextView
	private lateinit var detailButt: Button
	private lateinit var detail: DetailView
	private lateinit var pie: PieChartView
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		drawerLayout = findViewById(R.id.mainDrawer)
		optionButt = findViewById(R.id.mainOption)
		options = findViewById(R.id.optionMenu)
		money = findViewById(R.id.mMoney)
		detailButt = findViewById(R.id.mShowDetail)
		detail = findViewById(R.id.mDetailShow)
		pie = findViewById(R.id.mPie)
		
		data = File(filesDir.absolutePath + "/Data")
		tmpDir = File(filesDir.absolutePath + "/tmp")
		if(!tmpDir.exists()) tmpDir.mkdir()

		Config.setConfigFile(filesDir)
		
		if(savedInstanceState == null) {
			if( Config.getConfig("autoSync", Boolean::class.java) && resources.getBoolean(R.bool.allowSync)) {
				Handler(Looper.getMainLooper()).postDelayed({ syncFromFirebase() }, 1000)
				Handler(Looper.getMainLooper()).postDelayed({ record() }, 1000)
			}
			syncTotal()
			syncDetail()
			syncPie()
		}
		else {
			record()
		}
		
		options.setNavigationItemSelectedListener {
			when(it.itemId) {
				R.id.settings -> {
					val intent = Intent(this, Settings::class.java)
					startActivity(intent)
					true
				}
				R.id.settingsSt -> {
					val intent = Intent(this, AllOfRecord::class.java)
					startActivity(intent)

					true
				}
				else -> false
			}
		}
		
		optionButt.setOnClickListener { drawerLayout.openDrawer(options) }
		
	}
	
	override fun onStop() {
		super.onStop()
		Config.forceWrite()
	}
	
	override fun onResume() {
		super.onResume()
		record()
	}

	fun newRecord(v: View?) {
		val i = Intent(this, Create::class.java)
		startActivity(i)
	}
	
	fun state(v: View?) {
		val i = Intent(this, Statistics::class.java)
		startActivity(i)
	}

	private fun syncFromFirebase() {
		if((System.currentTimeMillis() - Config.getConfig("lastSync", Long::class.java)) < resources.getInteger(R.integer.syncInterval)) {
			return
		}
		Toast.makeText(this, R.string.syncStart, Toast.LENGTH_SHORT).show()
		Logger.i("syncFile", "start sync file")
		Config.setConfig("lastSync", System.currentTimeMillis())
		val yearDir = ref.list(5)
		while(!yearDir.isComplete);
		if(yearDir.isSuccessful) {
			val yearDirRef = yearDir.result?.prefixes
			if (yearDirRef != null) {
				for(yearRef in yearDirRef) {
					val monthData = yearRef.list(12)
					while(!monthData.isComplete);
					if(monthData.isSuccessful) {
						val monthDataRef = monthData.result?.items
						if(monthDataRef != null) {
							for(monthRef in monthDataRef) {
								val tmpFile = File.createTempFile("tmp" + monthRef.name, "json", tmpDir)
								monthRef.getFile(tmpFile).addOnSuccessListener {
									val fis = FileInputStream(tmpFile)
									val fos = FileOutputStream(getLocalDataPath(yearRef.name, monthRef.name))
									Logger.i("filePath", "tmpFile=\"${tmpFile.absolutePath}\", localFile=\"${getLocalDataPath(yearRef.name, monthRef.name)}\"")
									var c = fis.read()
									while(c != -1) {
										fos.write(c)
										c = fis.read()
									}
									fis.close()
									fos.close()
									if(!tmpFile.delete()) {
										Logger.e("deleteFile", "error when deleting tmp file")
									}
									Logger.i("syncFileDownload", "syncFile: ${data.absolutePath} successful")
								}.addOnFailureListener {
									Logger.e("syncFile", "error on: \n${it.stackTrace}")
									Toast.makeText(this, R.string.syncFalure, Toast.LENGTH_SHORT).show()
								}
							}
						}
					}
				}
			}
		}
		else {
			Logger.e("test", "${yearDir.exception}")
		}
	}

	private fun getLocalDataPath(dir: String, filename: String): File {
		Logger.i("getPathDebug", data.absolutePath + "/" + dir + "/" + filename)
		return File(data.absolutePath + "/" + dir + "/" + filename)
	}
	
	private fun record() {
		if((System.currentTimeMillis() - Config.getConfig("lastRecord", Long::class.java)) < resources.getInteger(R.integer.recordInterval)) {
			return
		}
		syncTotal()
		syncDetail()
		syncPie()
		Config.setConfig("lastRecord", System.currentTimeMillis())
	}
	
	private fun syncTotal() {
		var total = 0
		//sync
		val allDirs = data.listFiles()
		if(allDirs != null) {
			for(dir in allDirs) {
				val allFiles = dir.listFiles()
				if(allFiles != null) {
					for(file in allFiles) {
						val json = JsonStreamParser(FileReader(file)).next().asJsonArray
						for(obj in json) {
							if(obj.asJsonObject.get("type").asString == "income") {
								total += obj.asJsonObject.get("total").asInt
							}
							else {
								total -= obj.asJsonObject.get("total").asInt
							}
						}
					}
				}
			}
		}
		//set
		money.text = total.toString()
		Config.setConfig("total", total)
	}
	
	private fun syncDetail() {
		//check can show detail
		if(!Config.getConfig("hasDetail", Boolean::class.java)) {
			detailButt.isEnabled = false
			return
		}
		val detailArr = intArrayOf(0, 0, 0, 0, 0,  0, 0, 0, 0, 0)
		//sync
		val allDirs = data.listFiles()
		if(allDirs != null) {
			for(dir in allDirs) {
				val allFiles = dir.listFiles()
				if(allFiles != null) {
					for(file in allFiles) {
						val json = JsonStreamParser(FileReader(file)).next().asJsonArray
						for(obj in json) {
							if(!obj.asJsonObject.get("hasDetail").asBoolean) {
								Config.setConfig("hasDetail", false)
								detailButt.isEnabled = false
								return
							}
							val detailObj = obj.asJsonObject.get("detail").asJsonObject
							if(obj.asJsonObject.get("type").asString == "income") {
								detailArr[0] += detailObj.get("twoThousand").asInt
								detailArr[1] += detailObj.get("thousand").asInt
								detailArr[2] += detailObj.get("fiveHundred").asInt
								detailArr[3] += detailObj.get("twoHundred").asInt
								detailArr[4] += detailObj.get("hundred").asInt
								detailArr[5] += detailObj.get("fifty").asInt
								detailArr[6] += detailObj.get("twenty").asInt
								detailArr[7] += detailObj.get("ten").asInt
								detailArr[8] += detailObj.get("five").asInt
								detailArr[9] += detailObj.get("one").asInt
							}
							else {
								detailArr[0] -= detailObj.get("twoThousand").asInt
								detailArr[1] -= detailObj.get("thousand").asInt
								detailArr[2] -= detailObj.get("fiveHundred").asInt
								detailArr[3] -= detailObj.get("twoHundred").asInt
								detailArr[4] -= detailObj.get("hundred").asInt
								detailArr[5] -= detailObj.get("fifty").asInt
								detailArr[6] -= detailObj.get("twenty").asInt
								detailArr[7] -= detailObj.get("ten").asInt
								detailArr[8] -= detailObj.get("five").asInt
								detailArr[9] -= detailObj.get("one").asInt
							}
							
						}
					}
				}
			}
		}
		//set
		detailButt.setOnClickListener { when(detail.isVisible) { true -> detail.visibility = View.INVISIBLE;false -> detail.visibility = View.VISIBLE } }
		val detail = Config.getConfig("detail", JsonObject::class.java)
		
		this.detail.setValue(detail.get("twoThousand").asInt, detail.get("thousand").asInt, detail.get("fiveHundred").asInt,
				detail.get("twoHundred").asInt, detail.get("hundred").asInt, detail.get("fifty").asInt,
				detail.get("twenty").asInt, detail.get("ten").asInt, detail.get("five").asInt,
				detail.get("one").asInt)
	}
	
	private fun syncPie() {
		var foodCount = 0
		var transCount = 0
		var playCount = 0
		var usuallyCount = 0
		var otherCount = 0
		//sync
		val allDirs = data.listFiles()
		if(allDirs != null) {
			for(dir in allDirs) {
				val allFiles = dir.listFiles()
				if(allFiles != null) {
					for(file in allFiles) {
						val json = JsonStreamParser(FileReader(file)).next().asJsonArray
						for(obj in json) {
							when(obj.asJsonObject.get("tag").asString) {
								resources.getString(R.string.food) -> foodCount++
								resources.getString(R.string.trans) -> transCount++
								resources.getString(R.string.play) -> playCount++
								resources.getString(R.string.usually) -> usuallyCount++
								else -> otherCount++
							}
						}
					}
				}
			}
		}
		//set
		val sum = foodCount + transCount + playCount + usuallyCount + otherCount
		val foodPer = foodCount / sum.toFloat()
		val transPer = transCount / sum.toFloat()
		val playPer = playCount / sum.toFloat()
		val usuallyPer = usuallyCount / sum.toFloat()
		pie.setPieValue(foodPer, transPer, playPer, usuallyPer)
		Handler(Looper.getMainLooper()).postDelayed({findViewById<PieView>(R.id.pie_view).setValues(foodPer, transPer, playPer, usuallyPer)}, 1000)
		findViewById<PieView>(R.id.pie_view).setOnClickListener {
			//TODO: test
			val i = Intent(this, AllOfRecord::class.java)
			startActivity(i)
		}
	}

}