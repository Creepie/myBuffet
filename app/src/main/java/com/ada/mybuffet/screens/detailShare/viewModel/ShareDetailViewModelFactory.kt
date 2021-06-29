package com.ada.mybuffet.screens.detailShare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository

class ShareDetailViewModelFactory(private val shareDetailRepo: IShareDetailRepository, private val stockId: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ShareDetailViewModel(shareDetailRepo, stockId) as T
}