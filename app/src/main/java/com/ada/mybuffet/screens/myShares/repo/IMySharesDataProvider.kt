package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface IMySharesDataProvider {
    suspend fun getShareItems(): Flow<Resource<MutableList<ShareItem>>>
    suspend fun getShareItemsAsList(): Resource<MutableList<ShareItem>>
    suspend fun getTotalPortfolioValueHistory(): Flow<Resource<MutableList<PortfolioValueByDate>>>
    suspend fun getProfitLossOverviewData(): Flow<Resource<HashMap<String, BigDecimal>>>
}