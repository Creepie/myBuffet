package com.ada.mybuffet.screens.detailShare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem

class ShareDetailViewModelFactory(private val shareDetailRepo: IShareDetailRepository, private val shareItem: ShareItem) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ShareDetailViewModel(shareDetailRepo, shareItem) as T
}