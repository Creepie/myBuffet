package com.ada.mybuffet.repo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.math.BigDecimal
import java.math.RoundingMode

@Parcelize
data class StockShare(
    var name: String = "",
    var dividends: Dividends? = null,
    val symbol: String = "",
    var curPrice: Double = 0.0,
    var prevClosePrice: Double = 0.0
): Parcelable {

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

    fun getPercentDividendOfIndex(exDate: String): Double {
        var amount = 0.0
        val findAmount = dividends?.data?.findLast { it.exDate == exDate }?.amount
        if (findAmount != null){
            amount = findAmount
        }
        var percent = 0.0
        if (curPrice != 0.0){
            percent= amount / curPrice *100
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

    fun isPricePositive(): Boolean {
        return curPrice > prevClosePrice
    }

    fun isDividendIncreasing(): Boolean {
        val lastDividend = getLastDividend()
        var previousDividend = 0.0
        if (dividends?.data?.size ?: 0 > 2){
           previousDividend = dividends?.data?.get((dividends?.data?.size ?: 0) -2)?.amount ?: 0.0
        }
        return lastDividend > previousDividend

    }
}