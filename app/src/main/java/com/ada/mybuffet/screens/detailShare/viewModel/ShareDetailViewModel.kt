package com.ada.mybuffet.screens.detailShare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class ShareDetailViewModel(private val shareDetailRepo: IShareDetailRepository, private val stockId: String) : ViewModel() {

    val fetchPurchaseItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getPurchases(stockId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchSaleItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getSales(stockId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchFeeItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getFees(stockId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchDividendItemList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            shareDetailRepo.getDividends(stockId).collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    fun <T: Any> onItemSwiped(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.deleteItem(stockId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }

    }

    fun <T: Any> onUndoDeleteItem(item: T) = liveData(Dispatchers.IO) {
        try {
            emit(shareDetailRepo.addItem(stockId, item))
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }

    }

}