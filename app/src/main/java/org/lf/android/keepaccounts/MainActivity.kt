package org.lf.android.keepaccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.lf.android.keepaccounts.io.DataHandler

class MainActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		
		
	}
	
	fun newRecord(v: View?) {
		val i = Intent(this, Create::class.java)
		startActivity(i)
		
	}
	
}