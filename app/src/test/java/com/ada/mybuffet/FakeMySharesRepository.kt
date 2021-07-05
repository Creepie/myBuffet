package com.ada.mybuffet

import android.util.Log
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.screens.myShares.repo.IMySharesRepository
import com.ada.mybuffet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class FakeMySharesRepository : IMySharesRepository {

    private val TAG = "FAKE_MY_SHARES_REPOSITORY"

    val shareItemList = mutableListOf<ShareItem>()

    init {
        shareItemList.add(
            ShareItem(
                "fakeID1",
                "FKE1",
                "fakeStockName1",
                "10.00",
                "3.72",
                "10.00",
                "100.00",
                10,
                "5.00",
                "90.00")
        )
        shareItemList.add(ShareItem(
            "fakeID2",
            "FKE2",
            "fakeStockName2",
            "10.00",
            "3.72",
            "10.00",
            "100.00",
            10,
            "5.00",
            "90.00"))
        shareItemList.add(ShareItem(
            "fakeID3",
            "FKE3",
            "fakeStockName3",
            "10.00",
            "3.72",
            "10.00",
            "100.00",
            10,
            "5.00",
            "90.00"))
    }

    override suspend fun getShareItemsFromDB(): Flow<Resource<MutableList<ShareItem>>> = callbackFlow {

        // offer for flow (offer method is now deprecated, using trySend instead)
        trySend(Resource.Success(shareItemList)).isSuccess

        // close flow channel if not in use to avoid leaks
        awaitClose{}
    }

    override suspend fun getShareItemsAsListFromDB(): Resource<MutableList<ShareItem>> {
        // create a fake list of share items
        val shareItemList = mutableListOf<ShareItem>()
        shareItemList.add(
            ShareItem(
                "fakeID1",
                "FKE1",
                "fakeStockName1",
                "10.00",
                "3.72",
                "10.00",
                "100.00",
                10,
                "5.00",
                "90.00")
        )
        shareItemList.add(ShareItem(
            "fakeID2",
            "FKE2",
            "fakeStockName2",
            "10.00",
            "3.72",
            "10.00",
            "100.00",
            10,
            "5.00",
            "90.00"))
        shareItemList.add(ShareItem(
            "fakeID3",
            "FKE3",
            "fakeStockName3",
            "10.00",
            "3.72",
            "10.00",
            "100.00",
            10,
            "5.00",
            "90.00"))

        return Resource.Success<MutableList<ShareItem>>(shareItemList)
    }

    override suspend fun getProfitLossOverviewDataFromDB(): Flow<Resource<HashMap<String, BigDecimal>>> = callbackFlow {

        var profitLossOverviewData = hashMapOf<String, BigDecimal>(
            "totalProfit" to BigDecimal(100.00),
            "profitPercentage" to BigDecimal(3.72),
            "exchangeProfitLoss" to BigDecimal(20.00),
            "dividendProfit" to BigDecimal(10.00),
            "totalInvestment" to BigDecimal(90.00),
            "fees" to BigDecimal(5.99)
        )

        // offer for flow (offer method is now deprecated, using trySend instead)
        trySend(Resource.Success(profitLossOverviewData)).isSuccess

    }

    override suspend fun getTotalPortfolioValueHistoryFromDB(): Flow<Resource<MutableList<PortfolioValueByDate>>> = callbackFlow {

        val portfolioValueList = mutableListOf<PortfolioValueByDate>()
        portfolioValueList.add(PortfolioValueByDate(
            com.google.firebase.Timestamp(Date(1625392800000)), // 04 Jul 2021, 12:00:00
            "100.00")
        )
        portfolioValueList.add(PortfolioValueByDate(
            com.google.firebase.Timestamp(Date(1625306400000)), // 03 Jul 2021, 12:00:00
            "95.00")
        )
        portfolioValueList.add(PortfolioValueByDate(
            com.google.firebase.Timestamp(Date(1625220000000)), // 02 Jul 2021, 12:00:00
            "110.00")
        )
        portfolioValueList.add(PortfolioValueByDate(
            com.google.firebase.Timestamp(Date(1625133600000)), // 01 Jul 2021, 12:00:00
            "90.00")
        )


        // offer for flow (offer method is now deprecated, using trySend instead)
        trySend(Resource.Success(portfolioValueList)).isSuccess
    }
}