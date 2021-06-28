package com.ada.mybuffet.screens.detailShare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class ShareDetailViewModel(private val shareDetailRepo: IShareDetailRepository) : ViewModel() {

    val fetchPurchaseItemList = liveData(Dispatchers.IO) {
        // indicate that resource has started loading
        emit(Resource.Loading())

        try {
            shareDetailRepo.getPurchases().collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }

    val fetchSaleItemList = liveData(Dispatchers.IO) {
        // indicate that resource has started loading
        emit(Resource.Loading())

        try {
            shareDetailRepo.getSales().collect { resource ->
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Failure<Exception>(e))
        }
    }
}