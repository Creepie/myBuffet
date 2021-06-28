package com.ada.mybuffet.screens.detailShare.repo

import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IShareDetailRepository {
    suspend fun getPurchases(): Flow<Resource<MutableList<Purchase>>>

    suspend fun getSales(): Flow<Resource<MutableList<SaleItem>>>
}