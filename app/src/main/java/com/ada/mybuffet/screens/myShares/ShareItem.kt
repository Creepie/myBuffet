package com.ada.mybuffet.screens.myShares

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

data class ShareItem(
    val stockSymbol: String = "",
    val stockName: String = "",
    val currentPrice: String = "",
    val currentPricePercent: String = "",
    val totalDividends: String = "",
    val totalHoldings: String = ""
) {
    public fun getCurrentPricePercentFormatted(): String {
        return try {

            val price = BigDecimal(currentPrice)
            val pricePercent = BigDecimal(currentPricePercent)
            val formattedPercent = NumberFormat.getInstance(Locale.GERMANY).format(pricePercent)

            return if (price >= BigDecimal.ZERO) {
                "(+$formattedPercent%)"
            } else {
                "(-$formattedPercent%)"
            }
        } catch (e: NumberFormatException) {
            "n/a"
        }
    }

    public fun isPricePositive(): Boolean {
        return try {
            val price = BigDecimal(currentPrice)

            return price >= BigDecimal.ZERO
        } catch (e: NumberFormatException) {
            true // mark as positive in case of exceptions
        }
    }
}