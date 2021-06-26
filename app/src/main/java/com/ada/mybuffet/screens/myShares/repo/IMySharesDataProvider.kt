package com.ada.mybuffet.screens.myShares.repo

import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IMySharesDataProvider {
    suspend fun getShareItems(): Flow<Resource<MutableList<ShareItem>>>
}