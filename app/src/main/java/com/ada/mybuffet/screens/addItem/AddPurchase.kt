package com.ada.mybuffet.screens.addItem

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentAddItemBinding
import com.ada.mybuffet.databinding.FragmentAddSaleBinding
import com.ada.mybuffet.screens.addItem.repo.AddItemRepository
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModel
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModelFactory
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar
import java.util.*


class AddPurchase : Fragment(R.layout.fragment_add_item) {

    private val args: AddPurchaseArgs by navArgs()
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
        val binding = FragmentAddItemBinding.bind(view)

        binding.apply {
            //Set Symbol and name if passed in the argument
            if (shareItem != null) {
                addItemPurchaseInputSymbol.setText(shareItem!!.stockSymbol)
                addItemPurchaseInputSymbol.inputType = InputType.TYPE_NULL
                addItemPurchaseInputSymbol.setTextColor(Color.GRAY)
                addItemPurchaseInputName.setText(shareItem!!.stockName)
                addItemPurchaseInputName.inputType = InputType.TYPE_NULL
                addItemPurchaseInputName.setTextColor(Color.GRAY)
            }

            addItemPurchaseClose.setOnClickListener {
                view.findNavController().popBackStack()
            }

            addItemPurchaseSaveButton.setOnClickListener {
                handleSubmit(binding, viewModel, shareItem, view)
            }
        }
    }


    private fun handleSubmit(
        binding: FragmentAddItemBinding,
        viewModel: AddItemViewModel,
        shareItemVal: ShareItem?,
        view: View
    ) {
        binding.apply {
            var shareItem = shareItemVal
            val symbol = addItemPurchaseInputSymbol.text.toString()
            val name = addItemPurchaseInputName.text.toString()
            val price = addItemPurchaseInputPrice.text.toString()
            val numberString = addItemPurchaseInputNumber.text.toString()
            val fees = addItemPurchaseInputFees.text.toString()

            if (symbol.isNotEmpty()
                && name.isNotEmpty()
                && price.isNotEmpty()
                && numberString.isNotEmpty()
                && fees.isNotEmpty()
            ) {
                addItemPurchaseSaveButton.startAnimation()
                val number = numberString.toString().toInt()
                val purchaseItem = Purchase(
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
                viewModel.onFormSubmitted(purchaseItem, shareItem).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            val btnFillColor = activity?.let { it1 ->
                                ContextCompat.getColor(
                                    it1.applicationContext, R.color.actionColor
                                )
                            }
                            if (btnFillColor != null) {
                                addItemPurchaseSaveButton.doneLoadingAnimation(
                                    btnFillColor,
                                    BitmapFactory.decodeResource(
                                        resources,
                                        R.drawable.ic_done_white_48dp
                                    )
                                )
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                view.findNavController().popBackStack()
                            }, 600)
                        }
                        is Resource.Failure -> {
                            Snackbar.make(
                                requireView(),
                                "Stock Name could not be found",
                                Snackbar.LENGTH_LONG
                            ).show()
                            addItemPurchaseSaveButton.revertAnimation()
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