package org.lf.android.keepaccounts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.JsonObject
import org.lf.android.keepaccounts.io.HistoryFunc
import org.lf.android.keepaccounts.io.Logger
import org.lf.android.keepaccounts.view.HistoryView
import java.util.stream.Collectors

class Statistics : AppCompatActivity(), AdapterView.OnItemSelectedListener {

	private lateinit var goBack: ImageView
	private lateinit var yearList: IntArray
	
	private lateinit var yearMonth: Spinner
	private lateinit var historyList: ListView
	private lateinit var filter: Spinner
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_statistics)

		goBack = findViewById(R.id.statGoBack)
		historyList = findViewById(R.id.historyList)
		yearMonth = findViewById(R.id.yearChoose)
		filter = findViewById(R.id.filterChoose)
		
		filter.adapter = FilterSpinnerAdapter(this)
		filter.onItemSelectedListener = this
		
		yearMonth.adapter = YearMonthSpinnerAdapter(this)
		yearMonth.onItemSelectedListener = this
		
		//TODO: delete record
		historyList.adapter = ListContent(this, yearMonth.selectedItem.toString())
		
		goBack.setOnClickListener { onBackPressed() }

	}

	private fun <T> logErrorAndFinish(key: String): T? {
		Logger.e("showHistory", "no $key")
		finish()
		return null
	}

	private fun refreshList(filterPos: Int) {
		historyList.adapter = ListContent(this, yearMonth.selectedItem.toString(), Filter.getFilterFromPos(filterPos))
		historyList.invalidate()
	}

	override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		Handler(Looper.getMainLooper()).postDelayed({ refreshList(position) }, 1000)
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {

	}

	class ListContent(private val act: Activity, yearMonth: String, filter: Filter = Filter.Date): BaseAdapter(), SpinnerAdapter {
		
		private var year = 0
		private var month = 0
		
		private val historyList: List<JsonObject>
		private val actualHistory: List<JsonObject>
		
		init {
			deSerialize(yearMonth)
			val tmpList = HistoryFunc.getFileFromYearMonth(act.filesDir, year, month).toList()
			historyList = ArrayList(tmpList.size)
			for(item in tmpList) {
				historyList.add(item.asJsonObject)
			}
			//filter
			when(filter) {
				Filter.Date -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val ymd1 = o1.get("date").asString.split("/")
						val ymd2 = o2.get("date").asString.split("/")
						val y1 = ymd1[0].toInt()
						val m1 = ymd1[1].toInt()
						val d1 = ymd1[2].toInt()
						val y2 = ymd2[0].toInt()
						val m2 = ymd2[1].toInt()
						val d2 = ymd2[2].toInt()
						when {
							y1 > y2 -> 1
							y1 < y2 -> -1
							else -> {
								when {
									m1 > m2 -> 1
									m1 < m2 -> -1
									else -> {
										when {
											d1 > d2 -> 1
											d1 < d2 -> -1
											else -> 0
										}
									}
								}
							}
						}
					}.collect(Collectors.toList())
				}
				Filter.TimeStamp -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val t1 = o1.get("timeStamp").asLong
						val t2 = o2.get("timeStamp").asLong
						when {
							t1 > t2 -> 1
							t1 < t2 -> -1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
				Filter.Value -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val v1 = o1.get("total").asInt
						val v2 = o2.get("total").asInt
						when {
							v1 > v2 -> 1
							v1 < v2 -> -1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
				Filter.ValueReverse -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val v1 = o1.get("total").asInt
						val v2 = o2.get("total").asInt
						when {
							v1 > v2 -> -1
							v1 < v2 -> 1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
				Filter.Type -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val t1 = o1.get("type").asString
						val t2 = o2.get("type").asString
						when {
							t1 == "income" && t2 == "pay" -> -1
							t1 == "pay" && t2 == "income" -> 1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
				Filter.TypeReverse -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val t1 = o1.get("type").asString
						val t2 = o2.get("type").asString
						when {
							t1 == "income" && t2 == "pay" -> 1
							t1 == "pay" && t2 == "income" -> -1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
				Filter.Tag -> {
					actualHistory = historyList.stream().sorted { o1, o2 ->
						val c1 = o1.get("tag").asString[0]
						val c2 = o2.get("tag").asString[0]
						when {
							c1 > c2 -> 1
							c1 < c2 -> -1
							else -> 0
						}
					}.collect(Collectors.toList())
				}
			}
		}
		
		override fun getCount() = actualHistory.size
		
		override fun getItem(position: Int) = actualHistory[position]
		
		override fun getItemId(position: Int) = position.toLong()

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val context = act.applicationContext
			val obj = getItem(position)
			val view = HistoryView(context)
			val remarkS = obj.get("remark").asString.split("\n")
			var simpleRemark = remarkS[0]
			var isF = false
			if(simpleRemark.length > 10) {
				simpleRemark = simpleRemark.substring(0, 10) + "..."
				isF = true
			}
			if(remarkS.size > 1 && !isF) {
				simpleRemark += "..."
			}
			view.setAllStr(obj.get("tag").asString, obj.get("localeDate").asString, obj.get("total").asInt, simpleRemark, obj.get("type").asString)
			view.setOnClickListener { callDetailShow(position) }
			return view
		}
		
		private fun deSerialize(content: String) {
			val yearMonth = content.split("/")
			year = yearMonth[0].toInt()
			month = yearMonth[1].toInt()
		}

		private fun callDetailShow(pos: Int) {
			val i = Intent(act, HistoryShow::class.java)
			i.putExtra("content", actualHistory[pos].toString())
			act.startActivity(i)
		}

	}
	
	class YearMonthSpinnerAdapter(private val act: Activity): BaseAdapter(), SpinnerAdapter {
		
		private val allFiles = HistoryFunc.getFileShowName(act.filesDir)
		
		override fun getCount() = allFiles.size
		
		override fun getItem(position: Int) = allFiles[position]
		
		override fun getItemId(position: Int) = position.toLong()
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = TextView(act)
			view.text = getItem(position)
			view.setPadding(5, 5, 5, 5)
			return view
		}

		override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = TextView(act)
			view.text = getItem(position)
			view.setPadding(5, 5, 5, 5)
			view.background = act.resources.getDrawable(R.color.selected, null)
			view.textSize = 20f
			return view
		}
		
	}
	
	class FilterSpinnerAdapter(private val act: Activity): BaseAdapter(), SpinnerAdapter {
		
		private val filterString = act.resources.getStringArray(R.array.filter)
		
		override fun getCount() = filterString.size
		
		override fun getItem(position: Int) = filterString[position]
		
		override fun getItemId(position: Int) = position.toLong()
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = TextView(act)
			view.text = getItem(position)
			view.setPadding(5, 5, 5, 5)
			return view
		}

		override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = TextView(act)
			view.text = getItem(position)
			view.setPadding(5, 5, 5, 5)
			view.background = act.resources.getDrawable(R.color.selected, null)
			view.textSize = 20f
			return view
		}
		
	}
	
	enum class Filter {
		Date, TimeStamp, Value, ValueReverse, Type, TypeReverse, Tag;
		companion object {
			fun getFilterFromPos(pos: Int): Filter {
				return when(pos) {
					1 -> TimeStamp
					2 -> Value
					3 -> ValueReverse
					4 -> Type
					5 -> TypeReverse
					6 -> Tag
					else -> Date
				}
			}
		}
	}

}