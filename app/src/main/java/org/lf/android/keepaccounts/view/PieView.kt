package org.lf.android.keepaccounts.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import org.lf.android.keepaccounts.R

class PieView : ConstraintLayout {

	companion object {
		private const val sample = 360f
		private const val preCount = 1000
		private val colorArray = intArrayOf(
			Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.RED, Color.YELLOW

		)
	}

    private lateinit var pie: ImageView

    /**
     * sum of values needs equals 1, or under 1
     */
    private lateinit var values: FloatArray

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun setValues(vararg args: Float) {
        if(!checkValuesUnderOne(args)) throw IllegalArgumentException("values out of bound")
	    values = args;
	    drawPieWithAnimation()
    }

	fun setValueArray(args: FloatArray) {
		if(!checkValuesUnderOne(args)) throw IllegalArgumentException("values out of bound")
		values = args;
		drawPieWithAnimation()
	}

	fun setWidth(w: Int) {
		layoutParams = LayoutParams(w, height)
	}

	fun setHeight(h: Int) {
		layoutParams = LayoutParams(width, h)
	}

	fun setSize(w: Int, h: Int) {
		layoutParams = LayoutParams(w, h)
	}

	private fun checkValuesUnderOne(args: FloatArray): Boolean {
		var sum = 0f
		for(value in args) {
			sum += value
		}
		if(sum > 1) return false
		return true
	}

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.pie, this)
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        pie = findViewById(R.id.pie)

        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PieChartView,
                0, 0
            )
            //從Layout上 取得預設值
			val valuesStr = attributes.getString(R.styleable.PieView_values)?.replace(" ", "")?.split(",")?.map { it.toFloat() }?.toFloatArray()
	        if(valuesStr != null) {
	        	values = valuesStr
		        drawPieWithAnimation()
	        }


        }

    }

	private fun drawPieWithAnimation() {
		if(!this::values.isInitialized) throw RuntimeException("values not been initialized, cannot draw")
		if(values.size > colorArray.size) {
//			drawPieWithAnimationRandomColor()
			return
		}
		var degree = 0f
		val base = ResourcesCompat.getDrawable(resources, R.drawable.pie_chart_base, null)
		val paint = Paint(Paint.ANTI_ALIAS_FLAG)
		if(base != null) {
			val pieImg = base.toBitmap()
			val rect = RectF(0f, 0f, pieImg.width.toFloat(), pieImg.height.toFloat())
			val canvas = Canvas(pieImg)
			for((i, value) in values.withIndex()) {
				val ani = ValueAnimator.ofFloat(0f, value).also {
					it.duration = 1000//(value / 360 * 5000).toLong()
					it.repeatCount = 0
					it.interpolator = LinearInterpolator()
					val degreeToPie = degree
					it.addUpdateListener { va ->
						paint.color = colorArray[i]
						canvas.drawArc(rect, valueToDegree(degreeToPie), valueToDegree(va.animatedValue as Float), true, paint)
						val toPrint = BitmapDrawable(resources, pieImg)
						pie.setImageDrawable(toPrint)
						pie.invalidateDrawable(toPrint)
					}
				}
				ani.start()
				degree += value

			}
		}
		else {
			throw RuntimeException("base img not found")
		}
	}

    private fun drawPie() {
	    if(!this::values.isInitialized) throw RuntimeException("values not been initialized, cannot draw")

    }

//	private fun drawPieWithAnimationRandomColor() {
//		if(!this::values.isInitialized) throw RuntimeException("values not been initialized, cannot draw")
//		val step = (1 / sample * preCount).toInt()
//		var degree = 0f
//		val base = ResourcesCompat.getDrawable(resources, R.drawable.pie_chart_base, null)
//		val rand = Random()
//		val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//		if(base != null) {
//			val pieImg = base.toBitmap()
//			val rect = RectF(0f, 0f, pieImg.width.toFloat(), pieImg.height.toFloat())
//			val canvas = Canvas(pieImg)
//			for(value in values) {
//				val intValue = (value * preCount).toInt()
//				paint.color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
//				for(valueStep in 0 until intValue step step) {
//					canvas.drawArc(rect, degree, checkValueDegree(valueStep / preCount.toFloat(), value),true, paint)
//					val toPrint = BitmapDrawable(resources, pieImg)
//					pie.setImageDrawable(toPrint)
//					pie.invalidateDrawable(toPrint)
//				}
//				degree += value
//			}
//		}
//		else {
//			throw RuntimeException("base img not found")
//		}
//	}

	private fun checkValueDegree(value: Float, max: Float): Float {
		if(value >= max) return valueToDegree(max)
		return valueToDegree(value)
	}

	private fun valueToDegree(value: Float): Float {
		return value * 360
	}

}