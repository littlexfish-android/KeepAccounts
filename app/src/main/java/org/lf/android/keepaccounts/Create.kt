package org.lf.android.keepaccounts

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import org.lf.android.keepaccounts.io.Config
import org.lf.android.keepaccounts.io.DataHandler
import org.lf.android.keepaccounts.io.Files
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Create : AppCompatActivity(), TabLayout.OnTabSelectedListener  {

    private lateinit var mTab: TabLayout
    private lateinit var mPager: ViewPager
    
    private lateinit var backButt: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        
        mTab = findViewById(R.id.tab)
        mPager = findViewById(R.id.viewPager)
        
        backButt = findViewById(R.id.goBack)
        
        mTab.addOnTabSelectedListener(this)
        mTab.setupWithViewPager(mPager)
        mPager.adapter = MyPagerAdapter(this)
        
        backButt.setOnClickListener { this.onBackPressed() }
        
    }
    
    override fun onTabSelected(tab: TabLayout.Tab?) {
        mPager.currentItem = tab?.position ?: 0
    }
    
    override fun onTabUnselected(tab: TabLayout.Tab?) {
    
    }
    
    override fun onTabReselected(tab: TabLayout.Tab?) {
    
    }
    
    class MyPagerAdapter(private val act: Activity) : PagerAdapter() {

        private var year = 0
        private var month = 0
        private var day = 0

        private lateinit var payValue: EditText
        private lateinit var payTag: Spinner
        private lateinit var payDate: EditText
        private lateinit var payButt: Button
        private lateinit var payRemark: EditText
    
        private lateinit var incomeValue: EditText
        private lateinit var incomeTag: Spinner
        private lateinit var incomeDate: EditText
        private lateinit var incomeButt: Button
        private lateinit var incomeRemark: EditText
    
        override fun getCount() = 2
    
        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "pay"
                else -> "income"
            }
        }
        
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: View = when(position) {
                0 -> act.layoutInflater.inflate(R.layout.pay, container, false)
                else -> act.layoutInflater.inflate(R.layout.income, container, false)
            }
            when(position) {
                0 -> {
                    payValue = view.findViewById(R.id.payValue)
                    payTag = view.findViewById(R.id.payTag)
                    payDate = view.findViewById(R.id.payDate)
                    payButt = view.findViewById(R.id.payConfirm)
                    payRemark = view.findViewById(R.id.payRemark)
        
                    val d = Date()
                    val sdf = SimpleDateFormat.getDateInstance()
                    val calender = Calendar.getInstance()
                    calender.time = d
                    this.year = calender.get(Calendar.YEAR)
                    this.month = calender.get(Calendar.MONTH) + 1
                    this.day = calender.get(Calendar.DAY_OF_MONTH)

                    payDate.isFocusable = false
                    payDate.text.clear()
                    payDate.text.append(sdf.format(calender.time))
                    payDate.setOnClickListener {
                        val date = DatePickerDialog(act)
                        date.updateDate(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
                        date.setOnDateSetListener { _, year, month, dayOfMonth ->
                            calender.set(year, month, dayOfMonth)
                            payDate.text.clear()
                            payDate.text.append(sdf.format(calender.time))
                            this.year = year
                            this.month = month + 1
                            this.day = dayOfMonth
                        }
                        date.show()
                    }
                    
                    payTag.adapter = TagSpinner(act, getCustomTag())

                    payButt.setOnClickListener {
                        val data = DataHandler(payValue.text.toString().toInt(), System.currentTimeMillis(), payTag.selectedItem.toString(), payRemark.text.toString())
                        val file = Files(act.filesDir)
                        file.saveData(data, year, month, day, payDate.text.toString())
                        payValue.text.clear()
                        payRemark.text.clear()
                    }
        
                }
                else -> {
                    incomeValue = view.findViewById(R.id.incomeValue)
                    incomeTag = view.findViewById(R.id.incomeTag)
                    incomeDate = view.findViewById(R.id.incomeDate)
                    incomeButt = view.findViewById(R.id.incomeConfirm)
                    incomeRemark = view.findViewById(R.id.incomeRemark)
    
                    val d = Date()
                    val sdf = SimpleDateFormat.getDateInstance()
                    val calender = Calendar.getInstance()
                    calender.time = d
                    this.year = calender.get(Calendar.YEAR)
                    this.month = calender.get(Calendar.MONTH) + 1
                    this.day = calender.get(Calendar.DAY_OF_MONTH)

                    incomeDate.isFocusable = false
                    incomeDate.text.clear()
                    incomeDate.text.append(sdf.format(calender.time))
                    incomeDate.setOnClickListener {
                        val date = DatePickerDialog(act)
                        date.updateDate(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
                        date.setOnDateSetListener { _, year, month, dayOfMonth ->
                            calender.set(year, month, dayOfMonth)
                            incomeDate.text.clear()
                            incomeDate.text.append(sdf.format(calender.time))
                            this.year = year
                            this.month = month + 1
                            this.day = dayOfMonth
                        }
                        date.show()
                    }
    
                    incomeTag.adapter = TagSpinner(act, getCustomTag())

                    incomeButt.setOnClickListener {
                        val data = DataHandler(incomeValue.text.toString().toInt(), System.currentTimeMillis(), incomeTag.selectedItem.toString(), incomeRemark.text.toString())
                        val file = Files(act.filesDir)
                        file.saveData(data, year, month, day, incomeDate.text.toString(), false)
                        incomeValue.text.clear()
                        incomeRemark.text.clear()
                    }

                }
            }
            container.addView(view)
            return view
        }
        
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` == view
        }
    
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
        
        private fun getCustomTag(): ArrayList<String> {
            Config.setConfigFile(act.filesDir)
            val list = ArrayList<String>()
            val config = Config.getConfig("customTag", JsonArray::class.java)
            for(tag in config.asJsonArray) {
                list.add(tag.asString)
            }
            return list
        }
        
    }
    
    class TagSpinner(private val act: Activity, private val list: ArrayList<String>): BaseAdapter(), SpinnerAdapter {
        
        override fun getCount() = list.size + Config.defaultTag.size
    
        override fun getItem(position: Int): Any {
            return when(position) {
                in 0 until Config.defaultTag.size -> Config.defaultTag[position]
                in Config.defaultTag.size until list.size + Config.defaultTag.size -> list[Config.defaultTag.size + position]
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }
    
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = TextView(act)
            view.text = getItem(position).toString()
            view.textSize = 20f
            return view
        }
    
    
    }
    
}