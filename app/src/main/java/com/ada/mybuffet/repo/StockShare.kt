package com.ada.mybuffet.repo

import java.math.BigDecimal
import java.math.RoundingMode

data class StockShare(
    var name: String = "",
    var dividends: Dividends? = null,
    val symbol: String = "",
    var curPrice: Double = 0.0,
    var prevClosePrice: Double = 0.0
) {

    fun getLastDividend(): Double {
        return if (dividends == null || dividends!!.data.isEmpty()) {
            0.0
        } else {
            dividends!!.data.last().amount
        }
    }

    fun getPercentDividend(): Double {
        val lastDividend = getLastDividend()
        var percent = 0.0
        if (curPrice != 0.0){
            percent= lastDividend / curPrice *100
        }
        return BigDecimal(percent).setScale(2,RoundingMode.HALF_EVEN).toDouble()
    }

    fun getPercentStockPrice() :Double {
        return if (prevClosePrice != 0.0){
            BigDecimal(curPrice / prevClosePrice).setScale(2,RoundingMode.HALF_EVEN).toDouble()
        } else {
            0.00
        }
    }

}