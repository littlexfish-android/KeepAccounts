package org.lf.android.keepaccounts.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.android.keepaccounts.R

class DetailView : ConstraintLayout {

    private lateinit var countArr: IntArray
    private lateinit var viewArr: Array<TextView>

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun setValue(twoThousand: Int = 0, thousand: Int = 0, fiveHundred: Int = 0, twoHundred: Int = 0, hundred: Int = 0, fifty: Int = 0, twenty: Int = 0, ten: Int = 0, five: Int = 0, one: Int = 0) {
        countArr = intArrayOf(twoThousand, thousand, fiveHundred, twoHundred, hundred, fifty, twenty, ten, five, one)
        for((i, count) in countArr.withIndex()) {
            viewArr[i].text = resources.getString(R.string.detailSuffix, count)
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.detail_layout, this)
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        viewArr = arrayOf(findViewById(R.id.twoT), findViewById(R.id.T), findViewById(R.id.fiveH),
            findViewById(R.id.twoH), findViewById(R.id.H), findViewById(R.id.fifty),
            findViewById(R.id.twenty), findViewById(R.id.ten), findViewById(R.id.five),
            findViewById(R.id.one))

        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.HistoryView,
                0, 0
            )
            //從Layout上 取得預設值
            countArr = intArrayOf(attributes.getInt(R.styleable.DetailView_tT, 0), attributes.getInt(R.styleable.DetailView_T, 0), attributes.getInt(R.styleable.DetailView_fH, 0),
                attributes.getInt(R.styleable.DetailView_tH, 0), attributes.getInt(R.styleable.DetailView_H, 0), attributes.getInt(R.styleable.DetailView_ft, 0),
                attributes.getInt(R.styleable.DetailView_tt, 0), attributes.getInt(R.styleable.DetailView_t, 0), attributes.getInt(R.styleable.DetailView_f, 0),
                attributes.getInt(R.styleable.DetailView_o, 0))

            for((i, count) in countArr.withIndex()) {
                viewArr[i].text = resources.getString(R.string.detailSuffix, count)
            }

        }

    }

}