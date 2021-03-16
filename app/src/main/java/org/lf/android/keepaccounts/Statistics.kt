package org.lf.android.keepaccounts

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.google.gson.JsonObject
import org.lf.android.keepaccounts.io.HistoryFunc
import org.lf.android.keepaccounts.io.Logger

class Statistics : AppCompatActivity() {
	
	lateinit var yearList: IntArray
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_statistics)
		
		val extra = intent.extras
		
		if(extra == null) {
			Logger.e("showHistory", "no extra")
			finish()
		}
		
		if(extra != null) {
			yearList = (extra.getIntArray("year_months") ?: logErrorAndFinish("year_months")) as IntArray
		}
		
		
	}

	fun <T> logErrorAndFinish(key: String): T? {
		Logger.e("showHistory", "no $key")
		finish()
		return null
	}
	
	class ListContent(private val act: Activity, yearMonth: String, filter: Filter = Filter.Date): BaseAdapter(), SpinnerAdapter {
		
		var year = 0
		var month = 0
		
		private val historyList: List<JsonObject>
		private val allFiles = HistoryFunc.getFileShowName(act.filesDir)
		
		init {
			deSerialize(yearMonth)
			val tmpList = HistoryFunc.getFileFromYearMonth(act.filesDir, year, month).toList()
			historyList = ArrayList(tmpList.size)
			for(item in tmpList) {
				historyList.add(item.asJsonObject)
			}
		}
		
		override fun getCount() = historyList.size
		
		override fun getItem(position: Int) = allFiles[position]
		
		override fun getItemId(position: Int) = position.toLong()
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = TextView(act)
			view.text = getItem(position)
			view.textSize = 20f
			return view
		}
		
		private fun deSerialize(content: String) {
			val yearMonth = content.split("/")
			year = yearMonth[0].toInt()
			month = yearMonth[1].toInt()
		}
		
	}
	
	enum class Filter {
		Date, TimeStamp, Value, Type, Tag;
	}
	
}