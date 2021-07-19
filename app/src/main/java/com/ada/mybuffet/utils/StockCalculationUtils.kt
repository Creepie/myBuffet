package com.ada.mybuffet.utils

import com.ada.mybuffet.screens.detailShare.model.OverviewData

/**
 * @author Paul Pfisterer
 * Singleton that contains methods for calculating the profit and the value increase/decrease
 * of the current holdings
 */
object StockCalculationUtils {
    init {

    }

    /**
     * Calculates the Profit for one stock.
     * The method takes an OverviewData Object, that contains all necessary data for the calculation.
     * The profit is calculated, by getting the difference of the buys and the sales(without fees)
     * Then, the worth of the current holdings is calculated.
     * The difference of buy and sales is added/subtracted to/from the worth of the current holdings
     * At the end, the dividends are added, and all fees are substracted
     */
    fun calculateProfit(
        data: OverviewData,
    ): Double {
        //If no current Worth is passed return, a calculation is not possible
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


    /**
     * Calculates the worth increase/decrease of the currently owned shares when compared
     * to the price with which they were bought (average buy price is taken)
     */
    fun calculateExchangeBalance(
        data: OverviewData
    ): Double {
        //If no current Worth is passed return, a calculation is not possible
        if (data.currentWorth == 0.0) {
            return 0.0
        }

        //Calculate the average price for which one share was bought
        val pricePerPurchase = if (data.purchaseCount != 0) {
            data.purchaseSum / data.purchaseCount
        } else {
            0.0
        }

        //Calculate how many shares are still owned
        val stockHoldingAmount = data.purchaseCount - data.saleCount
        if (stockHoldingAmount < 0) {
            return 0.0
        }

        //Calculate the old worth, with the calculated average price and the currently owned amount of shares
        val oldHoldingsWorth = stockHoldingAmount * pricePerPurchase
        //Calculate the new worth with the current price and the currently owned amount of shares
        val currentHoldingWorth = stockHoldingAmount * data.currentWorth
        //calculate the difference
        return currentHoldingWorth - oldHoldingsWorth
    }

    /**
     * Calculate the relative increase/decrease with the investment sum and
     * the valueIncrease(Profit calculated with calculateProfit)
     */
    fun getProfitPercentage(investmentSum: Double, valueIncrease: Double): Double {
        return if (valueIncrease == 0.0 || investmentSum == 0.0) {
            0.0
        } else {
            (valueIncrease / investmentSum) * 100
        }
    }
}