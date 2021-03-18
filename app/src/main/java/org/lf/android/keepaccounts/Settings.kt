package org.lf.android.keepaccounts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.storage.FirebaseStorage
import org.lf.android.keepaccounts.io.Config
import org.lf.android.keepaccounts.io.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Settings : AppCompatActivity() {
	
	private val ref = FirebaseStorage.getInstance().reference.child("Data")
	private lateinit var data: File
	private lateinit var tmpDir: File

	private lateinit var goBack: ImageView
	private lateinit var autoSync: SwitchMaterial
	private lateinit var sync: Button

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		
		data = File(filesDir.absolutePath + "/Data")
		tmpDir = File(filesDir.absolutePath + "/tmp")
		if(!tmpDir.exists()) tmpDir.mkdir()

		autoSync = findViewById(R.id.autoSync)
		goBack = findViewById(R.id.settingsGoBack)
		sync = findViewById(R.id.settingSync)
		
		if(!resources.getBoolean(R.bool.allowSync)) {
			autoSync.isEnabled = false
			sync.isEnabled = false
		}
		else {
			autoSync.isChecked = Config.getConfig("autoSync", Boolean::class.java)
		}

		goBack.setOnClickListener { onBackPressed() }
		sync.setOnClickListener { sync() }

	}
	
	override fun onBackPressed() {
		super.onBackPressed()
		Config.setConfig("autoSync", autoSync.isChecked)
	}
	
	fun sync() {
		Toast.makeText(this, R.string.syncStart, Toast.LENGTH_SHORT).show()
		Logger.i("syncFile", "start sync file")
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
	
}