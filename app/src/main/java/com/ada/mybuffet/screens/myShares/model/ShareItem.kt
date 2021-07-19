package com.ada.mybuffet.screens.myShares.model

import android.os.Parcelable
import com.ada.mybuffet.screens.detailShare.model.OverviewData
import com.ada.mybuffet.utils.StockCalculationUtils
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class ShareItem (
    @DocumentId
    var shareItemId: String = "",
    val stockSymbol: String = "",
    val stockName: String = "",
    val currentPrice: String = "",
    val currentPricePercent: String = "",
    val totalDividends: String = "",
    val totalHoldings: String = "",
    val totalShareNumber: Int = 0,
    val totalFees: String = "",
    val totalInvestment: String = "",
    val totalSaleNumber: Int = 0,
    val totalPurchaseNumber: Int = 0,
    val totalPurchaseAmount: String = "0",
    val totalSaleAmount: String = "0",
) : Parcelable {
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
            val overviewData = OverviewData(
                purchaseCount = totalPurchaseNumber.toInt(),
                purchaseSum = totalPurchaseAmount.toDouble(),
                saleCount = totalSaleNumber.toInt(),
                saleSum = totalSaleAmount.toDouble(),
                feeSum = totalFees.toDouble(),
                dividendSum = totalDividends.toDouble(),
                currentWorth = currentPrice.toDouble()
            )
            val exchangeBalance = StockCalculationUtils.calculateExchangeBalance(overviewData)
            exchangeBalance > 0
        } catch (e: Exception) {
            true
        }
    }

    public fun oldIsInvestmentPositive(): Boolean {
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