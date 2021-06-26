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
    val totalHoldings: String = "",
    val totalFees: String = "",
    val totalInvestment: String = ""
) {
    public fun isPricePositive(): Boolean {
        return try {
            val price = BigDecimal(currentPricePercent)

            price >= BigDecimal.ZERO
        } catch (e: NumberFormatException) {
            true // mark as positive in case of exceptions
        }
    }

    public fun isInvestmentPositive(): Boolean {
        return try {
            val holdings = BigDecimal(totalHoldings)
            val investment = BigDecimal(totalInvestment)
            val dividends = BigDecimal(totalDividends)
            val fees = BigDecimal(totalFees)

            (holdings + dividends) >= (investment + fees)
        } catch (e: NumberFormatException) {
            true
        }
    }
}