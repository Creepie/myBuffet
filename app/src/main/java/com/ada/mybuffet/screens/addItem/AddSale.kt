package com.ada.mybuffet.screens.addItem

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentAddItemBinding
import com.ada.mybuffet.databinding.FragmentAddSaleBinding
import com.ada.mybuffet.screens.addItem.repo.AddItemRepository
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModel
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModelFactory
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddSale : Fragment(R.layout.fragment_add_sale) {
    private val args: AddItemArgs by navArgs()
    private val viewModel: AddItemViewModel by viewModels() {
        AddItemViewModelFactory(
            AddItemRepository(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Argument
        var shareItem = args.shareItem

        //Setup binding
        val binding = FragmentAddSaleBinding.bind(view)

        binding.apply {
            //Set Symbol and name if passed in the argument
            if (shareItem != null) {
                addItemSaleInputSymbol.setText(shareItem!!.stockSymbol)
                addItemSaleInputSymbol.inputType = InputType.TYPE_NULL
                addItemSaleInputSymbol.setTextColor(Color.GRAY)
                addItemSaleInputName.setText(shareItem!!.stockName)
                addItemSaleInputName.inputType = InputType.TYPE_NULL
                addItemSaleInputName.setTextColor(Color.GRAY)
            }


            addItemSaleClose.setOnClickListener {
                view.findNavController().popBackStack()
            }

            addItemSaleSaveButton.setOnClickListener {
                val symbol = addItemSaleInputSymbol.text.toString()
                val name = addItemSaleInputName.text.toString()
                val price = addItemSaleInputPrice.text.toString()
                val numberString = addItemSaleInputNumber.text.toString()
                val fees = addItemSaleInputFees.text.toString()

                if (symbol.isNotEmpty()
                    && name.isNotEmpty()
                    && price.isNotEmpty()
                    && numberString.isNotEmpty()
                    && fees.isNotEmpty()
                ) {
                    addItemSaleSaveButton.startAnimation()
                    val number = numberString.toString().toInt()
                    val saleItem = SaleItem(
                        date = Date(),
                        fees = fees,
                        sharePrice = price,
                        shareNumber = number
                    )
                    if (shareItem == null) {
                        shareItem = ShareItem(
                            stockName = name,
                            stockSymbol = symbol
                        )
                    }
                    viewModel.onFormSubmitted(saleItem, shareItem).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                view.findNavController().popBackStack()
                            }
                            is Resource.Failure -> {
                                addItemSaleSaveButton.revertAnimation()
                            }
                        }
                    }
                } else {
                    Snackbar.make(
                        requireView(),
                        "All Fields have to be filled",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}