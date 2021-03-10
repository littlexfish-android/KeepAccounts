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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Create : AppCompatActivity(), TabLayout.OnTabSelectedListener  {

    private lateinit var mTab: TabLayout
    private lateinit var mPager: ViewPager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        
        mTab = findViewById(R.id.tab)
        mPager = findViewById(R.id.viewPager)
    
        mTab.addOnTabSelectedListener(this)
        mTab.setupWithViewPager(mPager)
        mPager.adapter = MyPagerAdapter(this)
        
    }
    
    override fun onTabSelected(tab: TabLayout.Tab?) {
        mPager.currentItem = tab?.position ?: 0
    }
    
    override fun onTabUnselected(tab: TabLayout.Tab?) {
    
    }
    
    override fun onTabReselected(tab: TabLayout.Tab?) {
    
    }
    
    class MyPagerAdapter(private val act: Activity) : PagerAdapter() {
    
        private lateinit var payValue: TextView
        private lateinit var payTag: Spinner
        private lateinit var payDate: EditText
        private lateinit var payButt: Button
    
        private lateinit var incomeValue: TextView
        private lateinit var incomeTag: Spinner
        private lateinit var incomeDate: EditText
        private lateinit var incomeButt: Button
    
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
        
                    val d = Date()
                    val sdf = SimpleDateFormat.getDateInstance()
                    val calender = Calendar.getInstance()
                    calender.time = d
        
                    payDate.text.clear()
                    payDate.text.append(sdf.format(calender.time))
                    payDate.setOnClickListener {
                        val date = DatePickerDialog(act)
                        date.updateDate(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
                        date.setOnDateSetListener { view, year, month, dayOfMonth ->
                            calender.set(year, month, dayOfMonth)
                            payDate.text.clear()
                            payDate.text.append(sdf.format(calender.time))
                        }
                    }
                    
                    payTag.adapter = TagSpinner(act, getCustomTag())
        
                }
                else -> {
                    incomeValue = view.findViewById(R.id.incomeValue)
                    incomeTag = view.findViewById(R.id.incomeTag)
                    incomeDate = view.findViewById(R.id.incomeDate)
                    incomeButt = view.findViewById(R.id.incomeConfirm)
                    
                    
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
        
        fun getCustomTag(): ArrayList<String> {
            val list = ArrayList<String>()
            val config = Config.getConfig("customTag", JsonArray::class.java)
            for(tag in config!!.asJsonArray) {
                list.add(tag.asString)
            }
            return list
        }
        
        fun pay(v: View?) {
        
        }
    
        fun income(v: View?) {
        
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
            return view
        }
    
    
    }
    
}