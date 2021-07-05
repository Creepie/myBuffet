package com.ada.mybuffet.screens.myShares.viewModel

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ada.mybuffet.FakeMySharesRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.screens.myShares.repo.MySharesDataProvider
import com.ada.mybuffet.screens.myShares.repo.MySharesRepository
import com.ada.mybuffet.utils.NumberFormatUtils
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MySharesViewModelTest {

    @Test
    fun deleteMeLater() {
        assertEquals(2, 2)
    }

    /*
    private lateinit var viewModel: MySharesViewModel

    @Before
    fun setUp() {

        // "There's no need to use the delegate property or a ViewModelProvider,
        // you can just directly construct the ViewModel in unit tests."
        // according to: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-test-doubles#6
        viewModel = MySharesViewModelFactory(
                        MySharesDataProvider(
                            FakeMySharesRepository()    // inject fake repository
                        )
                    ).create(MySharesViewModel::class.java)
    }

    @Test
    fun testFetchShareItemList() {
        println("hello")

        // Create observer - no need for it to do anything!
        val observer = Observer<Resource<Any>> {}
        try {
            // Observe the LiveData forever
            viewModel.fetchShareItemList.observeForever(observer)

            // When adding a new task
            val value = viewModel.fetchShareItemList.value
            //assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
            println("hello2")
            println(value)
        } finally {
            // Whatever happens, don't forget to remove the observer!
            viewModel.fetchShareItemList.removeObserver(observer)
        }

    }

    @Test
    fun getFetchProfitLossOverviewData() = runBlocking {
        val repo = FakeMySharesRepository()

        repo.shareItemList.add(
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

        repo.getShareItemsFromDB().collect {
            when (it) {
                is Resource.Success -> {
                    val data = it.data
                    assertEquals("FKE1", data.first().stockSymbol)
                }
            }

        }

        repo.shareItemList.add(
            ShareItem(
                "fakeID5",
                "FKE5",
                "fakeStockName5",
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
    fun getFetchTotalPortfolioValueHistory() {
    }

     */
}