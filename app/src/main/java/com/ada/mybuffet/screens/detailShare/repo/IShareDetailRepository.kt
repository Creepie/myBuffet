package com.ada.mybuffet.screens.detailShare.repo

import com.ada.mybuffet.repo.SymbolPrice
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IShareDetailRepository {
    suspend fun getPurchases(stockId: String): Flow<Resource<MutableList<Purchase>>>
    suspend fun getSales(stockId: String): Flow<Resource<MutableList<SaleItem>>>
    suspend fun getFees(stockId: String): Flow<Resource<MutableList<FeeItem>>>
    suspend fun getDividends(stockId: String): Flow<Resource<MutableList<DividendItem>>>
    suspend fun getCurrentPrice(symbol: String) : SymbolPrice?

    suspend fun <T: Any> deleteItem(stockId: String, item: T) : Resource<T>
    suspend fun <T: Any> addItem(stockId: String, item: T) : Resource<T>
    suspend fun updatePriceInDB(stockId: String, price: SymbolPrice)
}