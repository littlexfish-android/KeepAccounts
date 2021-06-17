package org.lf.android.keepaccounts

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.android.keepaccounts.io.HistoryFunc
import org.lf.android.keepaccounts.view.PieView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class AllOfRecord : AppCompatActivity(), AdapterView.OnItemSelectedListener {

	private val spinnerCache = HashMap<CacheType, Cache>()
	private lateinit var year: Array<String>
	private lateinit var month: Array<String>

	private var cate = HistoryFunc.Category.Month

	private lateinit var pie: PieView

	private lateinit var categorySpinner: Spinner
	private lateinit var paramSpinner: Spinner
	private lateinit var pieSpinner: Spinner

	//TODO: Use PieView To Run Pie Chart
	//TODO: Also can Add Bar Chart
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_all_of_record)

		pie = findViewById(R.id.pieBase)
		categorySpinner = findViewById(R.id.statCatagory)
		paramSpinner = findViewById(R.id.statParam)
		pieSpinner = findViewById(R.id.statSpinner)

		month = HistoryFunc.getFileShowName(filesDir)
		val set = HashSet<String>()
		for(str in month) {
			set.add(str.split("/")[0])
		}
		year = set.toTypedArray()

		categorySpinner.adapter = CategorySpinnerAdapter(this)
		paramSpinner.adapter = ParamSpinnerAdapter(this, month)
		pieSpinner.adapter = PieSpinnerAdapter(this)

		categorySpinner.onItemSelectedListener = this
		paramSpinner.onItemSelectedListener = this
		pieSpinner.onItemSelectedListener = this


		findViewById<ImageView>(R.id.allRecordGoBack).setOnClickListener { onBackPressed() }

	}

	private fun pieChange() {
		val checkCache = CacheType(cate, paramSpinner.selectedItem as String, pieSpinner.selectedItem as String)
		if(spinnerCache.containsKey(checkCache)) {
			if(spinnerCache[checkCache]!!.isOverTime(60 * 1000)) {
				spinnerCache[checkCache] = Cache(HistoryFunc.getPercentage(filesDir, cate, paramSpinner.selectedItem as String, pieSpinner.selectedItem as String))
			}
		}
		else {
			spinnerCache[checkCache] = Cache(HistoryFunc.getPercentage(filesDir, cate, paramSpinner.selectedItem as String, pieSpinner.selectedItem as String))
		}
		pie.setValueArray(spinnerCache[checkCache]!!.floatArr)

	}

	class PieSpinnerAdapter(private val context: Context) : BaseAdapter(), SpinnerAdapter {

		override fun getCount() = 2

		override fun getItem(position: Int) = when(position) {
			0 -> "標籤"
			else -> "支出入"
		}

		override fun getItemId(position: Int) = position.toLong()

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val text = TextView(context)
			text.text = getItem(position)
			text.setPadding(5, 5, 5, 5)
			text.textSize = 20f
			return text
		}

	}

	class CategorySpinnerAdapter(private val context: Context) : BaseAdapter(), SpinnerAdapter {

		private val cate = HistoryFunc.Category.values()

		override fun getCount() = cate.size

		override fun getItem(position: Int) = cate[position]

		override fun getItemId(position: Int) = position.toLong()

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val text = TextView(context)
			text.text = getItem(position).showName
			text.setPadding(5, 5, 5, 5)
			text.textSize = 20f
			return text
		}

	}

	class ParamSpinnerAdapter(private val context: Context, private val arr: Array<String>) : BaseAdapter(), SpinnerAdapter {

		override fun getCount() = arr.size

		override fun getItem(position: Int) = arr[position]

		override fun getItemId(position: Int) = position.toLong()

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val text = TextView(context)
			text.text = getItem(position)
			text.setPadding(5, 5, 5, 5)
			text.textSize = 20f
			return text
		}

	}

	data class Cache(val floatArr: FloatArray, val timestamp: Long = System.currentTimeMillis()) {

		fun isOverTime(millis: Int) = (System.currentTimeMillis() - timestamp) >= millis

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Cache

			if (!floatArr.contentEquals(other.floatArr)) return false
			if (timestamp != other.timestamp) return false

			return true
		}

		override fun hashCode(): Int {
			var result = floatArr.contentHashCode()
			result = 31 * result + timestamp.hashCode()
			return result
		}

	}

	data class CacheType(val cate: HistoryFunc.Category, val param: String, val stat: String)

	override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		if (parent != null) {
			if (parent.id == R.id.statCatagory) {
				cate = categorySpinner.selectedItem as HistoryFunc.Category
				if(cate == HistoryFunc.Category.All) {
					paramSpinner.visibility = View.GONE
				}
				else {
					if(cate == HistoryFunc.Category.Month) {
						paramSpinner.adapter = ParamSpinnerAdapter(this, month)
						paramSpinner.invalidate()
					}
					else {
						paramSpinner.adapter = ParamSpinnerAdapter(this, year)
						paramSpinner.invalidate()
					}
					paramSpinner.visibility = View.VISIBLE
				}
			}
			pieChange()
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
		//Ignore
	}
}