package com.ada.mybuffet.repo.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ada.mybuffet.repo.FinnhubApi
import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.ShareDetailModel
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.repo.MySharesDataProvider
import com.ada.mybuffet.screens.myShares.repo.MySharesRepository
import com.ada.mybuffet.screens.stocks.StocksOverviewModel
import com.ada.mybuffet.utils.NumberFormatUtils
import com.ada.mybuffet.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class RefreshPortfolioTotalsWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val firestore = Firebase.firestore
    private val dataProvider = MySharesDataProvider(MySharesRepository())

    // used to get stock price
    private val shareDetailModel = ShareDetailModel()

    companion object {
        const val WORK_NAME = "com.ada.mybuffet.repo.worker.RefreshPortfolioTotalsWorker.kt"
    }


    override suspend fun doWork(): Result {
        Log.i("PORTFOLIO_WORKER_RUNNING", "Worker Refresh Portfolio Totals is running")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.i("PORTFOLIO_WORKER_RUNNING", "Could not get user id")
            return Result.retry()
        }

        // create reference to the target collection in firestore
        val userid = currentUser.uid
        val baseDocRef = firestore.collection("users").document(userid)

        when (val dataResource = dataProvider.getShareItemsAsList()) {
            is Resource.Loading -> {}

            is Resource.Success -> {
                var totalPortfolioValue = BigDecimal.ZERO
                val shareItemList = dataResource.data

                // STEP 1: update total holdings running sum for each stock in the portfolio
                for (shareItem in shareItemList) {

                    val totalHoldingsBeforeUpdate = shareItem.totalHoldings

                    try {
                        val currentPriceSymbol = shareDetailModel.getCurrentPrice(shareItem.stockSymbol)
                        if (currentPriceSymbol == null) {
                            Log.i("PORTFOLIO_WORKER_RUNNING", "Could not get stock price")
                            return Result.retry()
                        }

                        val currentPrice = currentPriceSymbol.c
                        val previousPrice = currentPriceSymbol.pc

                        if (currentPrice <= 0.0 || previousPrice <= 0.0) {
                            Log.i("PORTFOLIO_WORKER_RUNNING", "Invalid stock price")
                            return Result.retry()
                        }

                        // calc total holdings
                        val currentPriceBigDecimal = BigDecimal(currentPrice)
                        val totalShareNumberBigDecimal = BigDecimal(shareItem.totalShareNumber)
                        val totalHoldings = currentPriceBigDecimal.multiply(totalShareNumberBigDecimal)

                        if (totalHoldings < BigDecimal.ZERO) {
                            Log.i("PORTFOLIO_WORKER_RUNNING", "Invalid total calculated")
                            return Result.retry()
                        }

                        // calc price percent difference
                        val previousPriceBigDecimal = BigDecimal(previousPrice)
                        if (previousPriceBigDecimal == BigDecimal.ZERO) {
                            Log.i("PORTFOLIO_WORKER_RUNNING", "Invalid previous stock price")
                            return Result.retry()
                        }

                        val priceDiffPercentage =
                            BigDecimal(1).minus(
                                previousPriceBigDecimal.divide(currentPriceBigDecimal, 2, RoundingMode.HALF_UP)
                            ).multiply(BigDecimal(100))



                        // format and round values in order to store them in the database
                        val totalHoldingsRounded = totalHoldings.setScale(2, RoundingMode.HALF_UP)
                        val totalHoldingsAfterUpdate = totalHoldingsRounded.toString()

                        val currentPriceRounded = currentPriceBigDecimal.setScale(2, RoundingMode.HALF_UP)
                        val currentPriceStr = currentPriceRounded.toString()

                        val priceDiffPercentageRounded = priceDiffPercentage.setScale(2, RoundingMode.HALF_UP)
                        val priceDiffPercentageStr = priceDiffPercentageRounded.toString()

                        // update database fields
                        val shareDocRef = baseDocRef.collection("shares").document(shareItem.shareItemId)
                        shareDocRef.update(
                            "totalHoldings", totalHoldingsAfterUpdate,
                            "currentPrice", currentPriceStr,
                            "currentPricePercent", priceDiffPercentageStr
                        )

                        // increment total portfolio value
                        totalPortfolioValue = totalPortfolioValue.plus(totalHoldings)
                    } catch (e: HttpException) {
                        Log.i("PORTFOLIO_WORKER_RUNNING", "Refresh Stock on failure")
                        return Result.retry()
                    }
                }

                // STEP 2: add portfolio total value to history log
                val totalPortfolioValueRounded = totalPortfolioValue.setScale(2, RoundingMode.HALF_UP)
                val totalPortfolioValueStr = totalPortfolioValueRounded.toString()

                val historyData = hashMapOf(
                    "date" to Timestamp.now(),
                    "portfolioTotalValue" to totalPortfolioValueStr
                )
                val valueHistoryRef = baseDocRef.collection("portfolioValueHistory")
                valueHistoryRef.add(historyData)

                Log.i("PORTFOLIO_WORKER_RUNNING", "Updated portfolio successfully, total val: $totalPortfolioValueStr")
            }

            is Resource.Failure -> {
                Log.i("PORTFOLIO_WORKER_RUNNING", "Could not update portfolio totals")
                return Result.retry()
            }
        }

        return Result.success()
    }
}