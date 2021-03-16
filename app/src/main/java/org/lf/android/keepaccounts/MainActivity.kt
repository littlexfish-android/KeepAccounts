package org.lf.android.keepaccounts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import org.lf.android.keepaccounts.io.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

	private val ref = FirebaseStorage.getInstance().reference.child("Data")
	private lateinit var data: File
	private lateinit var tmpDir: File

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		data = File(filesDir.absolutePath + "/Data")
		tmpDir = File(filesDir.absolutePath + "/tmp")
		if(!tmpDir.exists()) tmpDir.mkdir()
		
		if(savedInstanceState == null) {
			Handler(Looper.getMainLooper()).postDelayed({syncFromFirebase()}, 1000)
		}

//		if(intent.extras?.containsKey("asd") != true) {
//			resources.configuration.setLocale(Locale.TAIWAN)
//			val i = intent
//			i.putExtra("asd", true)
//			finish()
//			startActivity(i)
//		}

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