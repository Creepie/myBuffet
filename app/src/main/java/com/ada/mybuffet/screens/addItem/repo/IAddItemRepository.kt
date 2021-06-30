package com.ada.mybuffet.screens.addItem.repo

import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.utils.Resource

interface IAddItemRepository {
    suspend fun <T: Any> addItem(stockId: String, item: T) : Resource<T>
    suspend fun addItemWithoutId(stockId: String, item: Purchase) : Resource<Purchase>
}