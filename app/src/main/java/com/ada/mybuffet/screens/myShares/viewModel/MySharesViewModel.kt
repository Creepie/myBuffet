package com.ada.mybuffet.screens.myShares.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.screens.myShares.repo.IMySharesDataProvider
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class MySharesViewModel(private val mySharesDataProvider: IMySharesDataProvider) : ViewModel() {


    val fetchShareItemList = liveData(Dispatchers.IO) {
        // indicate that resource has started loading
        emit(Resource.Loading())

        try {
            mySharesDataProvider.getShareItems().collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchProfitLossOverviewData = liveData(Dispatchers.IO) {
        // indicate that resource has started loading
        emit(Resource.Loading())

        try {
            mySharesDataProvider.getProfitLossOverviewData().collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchTotalPortfolioValueHistory = liveData(Dispatchers.IO) {
        // indicate that resource has started loading
        emit(Resource.Loading())

        try {
            mySharesDataProvider.getTotalPortfolioValueHistory().collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

}