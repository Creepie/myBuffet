package com.ada.mybuffet.utils

import com.ada.mybuffet.screens.detailShare.model.OverviewData

object StockCalculationUtils {
    init {

    }

    //sale number + amount
    //purchase number + amount

    fun calculateProfit(
        data: OverviewData,
    ): Double {
        //If no current Worth is passed, return
        if (data.currentWorth == 0.0) {
            return 0.0
        }

        //Calculate how much money is still in the shares
        //Can be negative, if the sales are bigger than the purchases -> in this case
        //they are added not subtracted later on
        val currentMoneyInShares = data.purchaseSum - data.saleSum

        //Calculate how many shares are still owned
        val currentAmountOfShares = data.purchaseCount - data.saleCount
        if (currentAmountOfShares < 0) {
            return 0.0
        }

        //Calculate the worth of the shares owned with the current price
        val currentSharesWorth = currentAmountOfShares * data.currentWorth
        //Subtract the money in the shares from the shares worth to get the profit
        val currentSharesProfit = currentSharesWorth - currentMoneyInShares


        return currentSharesProfit + data.dividendSum - data.saleFeeSum - data.feeSum - data.purchaseFeeSum
    }


    fun calculateExchangeBalance(
        data: OverviewData
    ): Double {
        if (data.currentWorth == 0.0) {
            return 0.0
        }

        val pricePerPurchase = if (data.purchaseCount != 0) {
            data.purchaseSum / data.purchaseCount
        } else {
            0.0
        }

        val stockHoldingAmount = data.purchaseCount - data.saleCount
        if (stockHoldingAmount < 0) {
            return 0.0
        }

        val oldHoldingsWorth = stockHoldingAmount * pricePerPurchase
        val currentHoldingWorth = stockHoldingAmount * data.currentWorth
        return currentHoldingWorth - oldHoldingsWorth
    }

    fun getProfitPercentage(investmentSum: Double, valueIncrease: Double): Double {
        return if (valueIncrease == 0.0 || investmentSum == 0.0) {
            0.0
        } else {
            (valueIncrease / investmentSum) * 100
        }
    }
}