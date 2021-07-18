package com.ada.mybuffet.screens.addItem.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.screens.addItem.repo.IAddItemRepository
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class AddItemViewModel(private val shareDetailRepo: IAddItemRepository) : ViewModel() {
    fun <T : Any> onFormSubmitted(item: T, shareItem: ShareItem?) = liveData(Dispatchers.IO) {
        if(shareItem != null && shareItem.shareItemId.isNotEmpty()) {
            try {
                emit(shareDetailRepo.addItem(shareItem, item))
            } catch (e: Exception) {
                emit(Resource.Failure<Exception>(e))
            }
        } else if(shareItem != null && item is Purchase) {
            try {
                emit(shareDetailRepo.addItemWithoutId(shareItem.stockSymbol, item))
            } catch (e: Exception) {
                emit(Resource.Failure<Exception>(e))
            }
        } else {
            emit(Resource.Failure<Exception>(Exception()))
        }
    }
}