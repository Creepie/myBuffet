package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.FakeMySharesRepository
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.util.*

class MySharesRepositoryTest {

    // use a fake instead of the real repository to make testing independent
    // from firestore calls
    private lateinit var repository: FakeMySharesRepository

    @Before
    fun setUp() {
        repository = FakeMySharesRepository()

        // add a share item
        repository.shareItemList.add(
            ShareItem(
                "fakeID4",
                "FKE4",
                "fakeStockName4",
                "10.00",
                "3.72",
                "10.00",
                "100.00",
                10,
                "5.00",
                "90.00")
        )
    }

    @Test
    fun testGetShareItemsFromDB() = runBlockingTest {
        // collect flow and store collected data in list
        var shareItemList = mutableListOf<ShareItem>()
        val job = launch {
            repository.getShareItemsFromDB().collect {
                when (it) {
                    is Resource.Success -> {
                        shareItemList.clear()
                        shareItemList.addAll(it.data)
                    }
                }
            }
        }


        // check if list is correct
        assertEquals("FKE1", shareItemList.first().stockSymbol)
        assertEquals("FKE4", shareItemList.last().stockSymbol)

        // cancel flow collection job again
        job.cancel()
    }

    @Test
    fun testGetShareItemsAsListFromDB() = runBlockingTest {
        val res = repository.getShareItemsAsListFromDB()

        var shareItemList = mutableListOf<ShareItem>()

        when (res) {
            is Resource.Success -> {
                shareItemList = res.data
            }
        }

        assertEquals("FKE1", shareItemList.first().stockSymbol)
    }

    @Test
    fun testGetProfitLossOverviewDataFromDB() = runBlockingTest {
        // collect flow and store collected data in map
        var profitLossOverviewData = hashMapOf<String, BigDecimal>()

        val job = launch {
            repository.getProfitLossOverviewDataFromDB().collect {
                when (it) {
                    is Resource.Success -> {
                        profitLossOverviewData = it.data
                    }
                }
            }
        }


        // check if data is correct
        assertEquals(BigDecimal(100.00), profitLossOverviewData["totalProfit"])
        assertEquals(BigDecimal(3.72), profitLossOverviewData["profitPercentage"])
        assertEquals(BigDecimal(20.00), profitLossOverviewData["exchangeProfitLoss"])
        assertEquals(BigDecimal(10.00), profitLossOverviewData["dividendProfit"])
        assertEquals(BigDecimal(90.00), profitLossOverviewData["totalInvestment"])
        assertEquals(BigDecimal(5.99), profitLossOverviewData["fees"])


        // cancel flow collection job again
        job.cancel()
    }

    @Test
    fun testGetTotalPortfolioValueHistoryFromDB() = runBlockingTest {
        // collect flow and store collected data in list
        var portfolioValueList = mutableListOf<PortfolioValueByDate>()

        val job = launch {
            repository.getTotalPortfolioValueHistoryFromDB().collect {
                when (it) {
                    is Resource.Success -> {
                        portfolioValueList = it.data
                    }
                }
            }
        }


        assertEquals(Timestamp(Date(1625392800000)), portfolioValueList.first().date)
        assertEquals("100.00", portfolioValueList.first().portfolioTotalValue)

        // cancel flow collection job again
        job.cancel()
    }
}