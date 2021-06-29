package com.ada.mybuffet.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.ada.mybuffet.R
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.DateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class MPChartCustomMarkerView(
    context: Context?,
    layoutResource: Int,
    portfolioValuesByDate: MutableList<PortfolioValueByDate>
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val portfolioValuesByDate: MutableList<PortfolioValueByDate> = portfolioValuesByDate

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        val arrayIndex = e.x.toInt()

        val timestamp = portfolioValuesByDate[arrayIndex].date
        val date = timestamp.toDate()

        val formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(date)
        tvContent.text = formattedDate

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}