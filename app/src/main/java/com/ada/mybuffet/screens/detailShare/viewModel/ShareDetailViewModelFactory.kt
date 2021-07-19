package com.ada.mybuffet.screens.detailShare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.detailShare.repo.IShareDetailRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem

/**
 * @author Paul Pfisterer
 * Factory for the ShareDetail ViewModel
 * needed for passing arguments to the view model: The shareDetailRepo and the shareItem(Representing
 * the current stock) are passed.
 */
class ShareDetailViewModelFactory(private val shareDetailRepo: IShareDetailRepository, private val shareItem: ShareItem) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ShareDetailViewModel(shareDetailRepo, shareItem) as T
}