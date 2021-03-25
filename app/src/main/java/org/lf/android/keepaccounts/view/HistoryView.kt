package org.lf.android.keepaccounts.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.android.keepaccounts.R

class HistoryView : ConstraintLayout {

	private var tag = "Tag"
	private var date = "Date"
	private var value = 0
	private var remark = "Remark"
	private var type = "pay"

	private lateinit var tagView: TextView
	private lateinit var dateView: TextView
	private lateinit var valueView: TextView
	private lateinit var remarkView: TextView
	private lateinit var typeView: TextView


	constructor(context: Context) : super(context) {
		init(context, null)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(context, attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(context, attrs)
	}

	private fun setDefault() {
		tag = "Tag"
		date = "Date"
		value = 0
		remark = "Remark"
		type = "pay"
	}

	fun setAllStr(tag: String, date: String, value: Int, remark: String, type: String) {
		checkType(type)
		this.tag = tag
		this.date = date
		this.value = value
		this.remark = remark
		this.type = type
		tagView.text = tag
		dateView.text = date
		valueView.text = "\$$value"
		remarkView.text = remark
		typeView.text = type
	}

	private fun checkType(type: String) {
		when(type) {
			in arrayOf("income", "pay") -> {}
			else -> {
				throw IllegalArgumentException("Type Error")
			}
		}
	}

	private fun init(context: Context, attrs: AttributeSet?) {
		setDefault()
		View.inflate(context, R.layout.history_layout, this)
		descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

		tagView = findViewById(R.id.showTag)
		dateView = findViewById(R.id.showDate)
		valueView = findViewById(R.id.showValue)
		remarkView = findViewById(R.id.showRemark)
		typeView = findViewById(R.id.showType)

		if (attrs != null) {
			val attributes = context.theme.obtainStyledAttributes(
				attrs,
				R.styleable.HistoryView,
				0, 0
			)
			//從Layout上 取得預設值
			tagView.text = attributes.getString(R.styleable.HistoryView_tag)
			dateView.text = attributes.getString(R.styleable.HistoryView_date)
			valueView.text = "\$${attributes.getInt(R.styleable.HistoryView_value, 0)}"
			remarkView.text = attributes.getString(R.styleable.HistoryView_remark)
			val typeStr = attributes.getInt(R.styleable.HistoryView_type, -1)
			typeView.text = when(typeStr) {
				0 -> context.resources.getString(R.string.`in`)
				1 -> context.resources.getString(R.string.out)
				else -> ""
			}
		}

	}

}