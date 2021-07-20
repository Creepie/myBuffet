package com.ada.mybuffet.screens.addItem.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ada.mybuffet.screens.addItem.repo.IAddItemRepository

/**
 * @author Paul Pfisterer
 * Factory for the AddItem ViewModel
 * needed for passing arguments to the view model: The addItemRepo is passed.
 */
class AddItemViewModelFactory(private val addItemRepo: IAddItemRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AddItemViewModel(addItemRepo) as T
}