package com.ada.mybuffet.screens.detailShare.repo

import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IShareDetailRepository {
    suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>>
    suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>>

    suspend fun <T: Any> deleteItem(stockId: String, item: T) : Resource<T>
    suspend fun <T: Any> addItem(stockId: String, item: T) : Resource<T>
}