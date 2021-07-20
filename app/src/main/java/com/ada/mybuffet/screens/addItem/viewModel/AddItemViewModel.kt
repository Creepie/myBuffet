package com.ada.mybuffet.screens.addItem.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ada.mybuffet.screens.addItem.repo.IAddItemRepository
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

/**
 * @author Paul Pfisterer
 * View Model for the AddItem Views (AddDividend, AddFee, AddPurchase, AddSale)
 * Takes the addItem as argument
 */
class AddItemViewModel(private val addItemRepo: IAddItemRepository) : ViewModel() {
    /**
     * Function, that is invoked when the form to add an item is submitted
     * The type of the item can be purchase, sale, fee or dividend
     * In the case of the purchase, the adding can be done from the start page, this means
     * that the id of the stock is unknown and the addItemWithoutId is used
     */
    fun <T : Any> onFormSubmitted(item: T, shareItem: ShareItem?) = liveData(Dispatchers.IO) {
        //check if the id was passed
        if(shareItem != null && shareItem.shareItemId.isNotEmpty()) {
            try {
                //try to add the item via the addItem method
                emit(addItemRepo.addItem(shareItem, item))
            } catch (e: Exception) {
                emit(Resource.Failure<Exception>(e))
            }
        } else if(shareItem != null && item is Purchase) {
            try {
                //id is unknown, add via the addITemWithoutId method
                emit(addItemRepo.addPurchaseWithoutId(shareItem.stockSymbol, item))
            } catch (e: Exception) {
                emit(Resource.Failure<Exception>(e))
            }
        } else {
            emit(Resource.Failure<Exception>(Exception()))
        }
    }
}