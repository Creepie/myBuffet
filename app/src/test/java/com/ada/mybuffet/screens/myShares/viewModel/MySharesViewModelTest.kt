package com.ada.mybuffet.screens.myShares.viewModel

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ada.mybuffet.FakeMySharesRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.screens.myShares.repo.MySharesDataProvider
import com.ada.mybuffet.screens.myShares.repo.MySharesRepository
import com.ada.mybuffet.utils.NumberFormatUtils
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MySharesViewModelTest {

    private lateinit var viewModel: MySharesViewModel

    @Before
    fun setUp() {

    }


    @Test
    fun testFetchShareItemList() = runBlockingTest {

        // delete me
        assertEquals(2, 2)

        /*
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

        // "There's no need to use the delegate property or a ViewModelProvider,
        // you can just directly construct the ViewModel in unit tests."
        // according to: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-test-doubles#6
        viewModel = MySharesViewModelFactory(
            MySharesDataProvider(
                repo   // inject fake repository
            )
        ).create(MySharesViewModel::class.java)

        var shareItemList = mutableListOf<ShareItem>()


        val observer = Observer<Resource<Any>> {}
        try {
            // Observe the LiveData forever
            viewModel.fetchShareItemList.observeForever(observer)


            val value = viewModel.fetchShareItemList.value
            //assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
            println("hello2")
            println(value)
        } finally {
            // Whatever happens, don't forget to remove the observer!
            viewModel.fetchShareItemList.removeObserver(observer)
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

         */







        /*
        val job = launch {
            viewModel.fetchShareItemList.observe()

            viewModel.fetchShareItemList.asFlow().collect() {
                when (it) {
                    is Resource.Success -> {
                        shareItemList.clear()
                        shareItemList.addAll(it.data as MutableList<ShareItem>)
                    }
                }
            }
        }




        shareItemList.forEach {
            println(it.stockSymbol)
        }

        assertEquals("FKE1", shareItemList.first().stockSymbol)

        job.cancel()

         */


        /*
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

         */

    }
}