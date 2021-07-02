package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface IMySharesRepository {
    suspend fun getShareItemsFromDB(): Flow<Resource<MutableList<ShareItem>>>
    suspend fun getShareItemsAsListFromDB(): Resource<MutableList<ShareItem>>
    suspend fun getProfitLossOverviewDataFromDB(): Flow<Resource<HashMap<String, BigDecimal>>>
    suspend fun getTotalPortfolioValueHistoryFromDB(): Flow<Resource<MutableList<PortfolioValueByDate>>>
}