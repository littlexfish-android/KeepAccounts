package org.lf.android.keepaccounts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.JsonStreamParser
import org.lf.android.keepaccounts.io.Logger
import org.lf.android.keepaccounts.view.DetailView

class HistoryShow : AppCompatActivity() {

    lateinit var detail: DetailView
    lateinit var detailButt: Button
    lateinit var type: TextView
    lateinit var tag: TextView
    lateinit var value: TextView
    lateinit var date: TextView
    lateinit var remark: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_show)

        detail = findViewById(R.id.detailShow)
        detailButt = findViewById(R.id.hShowDetail)
        type = findViewById(R.id.hType)
        tag = findViewById(R.id.hTag)
        value = findViewById(R.id.hValue)
        date = findViewById(R.id.hDate)
        remark = findViewById(R.id.hRemark)

        if(intent.extras == null) {
            Logger.e("detailShow", "no extra")
            Toast.makeText(this, "no content", Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            val content = JsonStreamParser(intent.extras!!.getString("content")).next().asJsonObject
            if(content.get("hasDetail").asBoolean) {
                val detail = content.get("detail").asJsonObject
                this.detail.setValue(detail.get("twoThousand").asInt, detail.get("thousand").asInt, detail.get("fiveHundred").asInt,
                    detail.get("twoHundred").asInt, detail.get("hundred").asInt, detail.get("fifty").asInt,
                    detail.get("twenty").asInt, detail.get("ten").asInt, detail.get("five").asInt,
                    detail.get("one").asInt)
            }
            else {
                detailButt.isEnabled = false
            }
            val typeOut = if(content.get("type").asString == "income") resources.getString(R.string.`in`)
            else resources.getString(R.string.out)
            type.text = typeOut
            tag.text = content.get("tag").asString
            value.text = content.get("total").asInt.toString()
            date.text = content.get("localeDate").asString
            remark.text = content.get("remark").asString
        }

        detailButt.setOnClickListener { when(detail.isVisible) { true -> detail.visibility = View.INVISIBLE; false -> View.VISIBLE } }

        findViewById<ImageView>(R.id.historyGoBack).setOnClickListener { onBackPressed() }

    }

}