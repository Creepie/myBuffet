package com.ada.mybuffet.screens.addItem.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.addItem.repo.IAddItemRepository

class AddItemViewModelFactory(private val shareDetailRepo: IAddItemRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddItemViewModel(shareDetailRepo) as T
}