package org.lf.android.keepaccounts.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import org.lf.android.keepaccounts.R

class PieChartView : ConstraintLayout {
	
	private lateinit var pie: ImageView
	
	private lateinit var foodT: TextView
	private lateinit var transT: TextView
	private lateinit var playT: TextView
	private lateinit var usuallyT: TextView
	private lateinit var otherT: TextView
	
	private var foodValue = 0f
	private var transValue = 0f
	private var playValue = 0f
	private var usuallyValue = 0f
	private var otherValue = 0f
	
	constructor(context: Context) : super(context) {
		init(context, null)
	}
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(context, attrs)
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(context, attrs)
	}
	
	fun setPieValue(food: Float, trans: Float, play: Float, usually: Float) {
		if(checkOutValueBound(food, trans, play, usually)) {
			throw IllegalArgumentException("sum of values out of bound(SUM > 1)")
		}
		foodValue = food
		transValue = trans
		playValue = play
		usuallyValue = usually
		otherValue = 1f - foodValue - transValue - playValue - usuallyValue
		
		val showText = resources.getString(R.string.showPieDetail, resources.getString(R.string.food), foodValue * 100, resources.getString(R.string.trans), transValue * 100,
				resources.getString(R.string.play), playValue * 100, resources.getString(R.string.usually), usuallyValue * 100, resources.getString(R.string.other),otherValue * 100)
		pie.setOnLongClickListener { Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();true }
		
		drawPie()
		
	}
	
	private fun checkOutValueBound(vararg values: Float): Boolean {
		var sum = 0f
		for(value in values) {
			sum += value
		}
		return sum > 1
	}
	
	private fun init(context: Context, attrs: AttributeSet?) {
		View.inflate(context, R.layout.pie_chart, this)
		descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
		
		pie = findViewById(R.id.pieChart)
		foodT = findViewById(R.id.pieFood)
		transT = findViewById(R.id.pieTrans)
		playT = findViewById(R.id.piePlay)
		usuallyT = findViewById(R.id.pieUsually)
		otherT = findViewById(R.id.pieOther)
		
		if (attrs != null) {
			val attributes = context.theme.obtainStyledAttributes(
					attrs,
					R.styleable.PieChartView,
					0, 0
			)
			//從Layout上 取得預設值
			var tmp = attributes.getString(R.styleable.PieChartView_food)
			foodT.text = tmp ?: resources.getString(R.string.food)
			tmp = attributes.getString(R.styleable.PieChartView_trans)
			transT.text = tmp ?: resources.getString(R.string.trans)
			tmp = attributes.getString(R.styleable.PieChartView_play)
			playT.text = tmp ?: resources.getString(R.string.play)
			tmp = attributes.getString(R.styleable.PieChartView_usually)
			usuallyT.text = tmp ?: resources.getString(R.string.usually)
			tmp = attributes.getString(R.styleable.PieChartView_other)
			otherT.text = tmp ?: resources.getString(R.string.other)
			
			foodValue = attributes.getFraction(R.styleable.PieChartView_food_value, 1, 1, 0f)
			transValue = attributes.getFraction(R.styleable.PieChartView_food_value, 1, 1, 0f)
			playValue = attributes.getFraction(R.styleable.PieChartView_food_value, 1, 1, 0f)
			usuallyValue = attributes.getFraction(R.styleable.PieChartView_food_value, 1, 1, 0f)
			otherValue = 1f - foodValue - transValue - playValue - usuallyValue
			
			drawPie()
			
		}
		
		val showText = resources.getString(R.string.showPieDetail, resources.getString(R.string.food), foodValue / 100, resources.getString(R.string.trans), transValue / 100,
				resources.getString(R.string.play), playValue / 100, resources.getString(R.string.usually), usuallyValue / 100, resources.getString(R.string.other),otherValue / 100)
		pie.setOnLongClickListener { Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();true }
		
	}
	
	private fun drawPie() {
		
		val pieDrawable = ResourcesCompat.getDrawable(resources, R.drawable.pie_chart_base, null)
		if(pieDrawable != null) {
			val bit = pieDrawable.toBitmap()
			val canvas = Canvas(bit)
			val rect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
			var angle = 0f
			val paint = Paint(Paint.ANTI_ALIAS_FLAG)
			
			//food
			paint.color = resources.getColor(R.color.pie_food, null)
			canvas.drawArc(rect, calPercentageToDegrees(angle), calPercentageToDegrees(foodValue), true, paint)
			angle += foodValue
			
			//trans
			paint.color = resources.getColor(R.color.pie_trans, null)
			canvas.drawArc(rect, calPercentageToDegrees(angle), calPercentageToDegrees(transValue), true, paint)
			angle += transValue
			
			//play
			paint.color = resources.getColor(R.color.pie_play, null)
			canvas.drawArc(rect, calPercentageToDegrees(angle), calPercentageToDegrees(playValue), true, paint)
			angle += playValue
			
			//usually
			paint.color = resources.getColor(R.color.pie_usually, null)
			canvas.drawArc(rect, calPercentageToDegrees(angle), calPercentageToDegrees(usuallyValue), true, paint)
			
			//toPie
			canvas.save()
			canvas.restore()
			val bitmap = BitmapDrawable(resources, bit)
			pie.setImageDrawable(bitmap)
			pie.invalidateDrawable(bitmap)
			
			
			return
		}
		throw IllegalStateException("pie chart base not found")
	}
	
	/**
	 * @param percentage - percentage to float, e.x 100% -> 1, 10% -> 0.1
	 */
	private fun calPercentageToDegrees(percentage: Float): Float {
		return 360 * percentage
	}
	
}