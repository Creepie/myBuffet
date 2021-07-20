package com.ada.mybuffet.screens.addItem

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
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
import com.ada.mybuffet.screens.home.Home
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.DatabaseException
import com.ada.mybuffet.utils.InvalidSalePurchaseBalanceException
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * @author Paul Pfisterer
 * View for the Add Sale Screen
 */
class AddSale : Fragment(R.layout.fragment_add_sale) {
    //Arguments passed via the navigation, includes the shareItem
    private val args: AddSaleArgs by navArgs()

    //Lazy creation of the View model via the Factory with the required arguments
    private val viewModel: AddItemViewModel by viewModels() {
        AddItemViewModelFactory(
            AddItemRepository(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Argument
        val shareItem = args.shareItem

        //Setup binding
        val binding = FragmentAddSaleBinding.bind(view)

        binding.apply {
            //Set Symbol and prevent input for symbol and name
            addItemSaleInputSymbol.setText(shareItem.stockSymbol)
            addItemSaleInputSymbol.inputType = InputType.TYPE_NULL
            addItemSaleInputSymbol.setTextColor(Color.GRAY)
            addItemSaleInputName.setText(shareItem.stockName)
            addItemSaleInputName.inputType = InputType.TYPE_NULL
            addItemSaleInputName.setTextColor(Color.GRAY)

            //Set on click listeners
            addItemSaleClose.setOnClickListener {
                view.findNavController().popBackStack()
            }

            addItemSaleSaveButton.setOnClickListener {
                handleSubmit(binding, viewModel, shareItem, view)
            }
        }
    }

    /**
     * This function is invoked, when the form is submitted
     * The input is validated.
     * If everythings correct, call the onFormSubmitted method of the ViewModel
     * If not, display a Snackbar with an error message
     */
    private fun handleSubmit(
        binding: FragmentAddSaleBinding,
        viewModel: AddItemViewModel,
        shareItem: ShareItem,
        view: View
    ) {
        binding.apply {
            //Get the input
            val symbol = addItemSaleInputSymbol.text.toString()
            val name = addItemSaleInputName.text.toString()
            val price = addItemSaleInputPrice.text.toString()
            val numberString = addItemSaleInputNumber.text.toString()
            val fees = addItemSaleInputFees.text.toString()
            //Check if everything is filled out
            if (symbol.isEmpty()
                || name.isEmpty()
                || price.isEmpty()
                || numberString.isEmpty()
                || fees.isEmpty()
            ) {
                //Invalid Input, Display error snackbar
                Snackbar.make(
                    requireView(),
                    "All Fields have to be filled",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                //Valid Input, start Animation, Create saleItem
                addItemSaleSaveButton.startAnimation()
                val number = numberString.toString().toInt()
                val saleItem = SaleItem(
                    date = Date(),
                    fees = fees,
                    sharePrice = price,
                    shareNumber = number
                )
                //Call the method of the view model with the saleItem and the shareItem
                viewModel.onFormSubmitted(saleItem, shareItem).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            //Animate Success
                            val btnFillColor = activity?.let { it1 ->
                                ContextCompat.getColor(
                                    it1.applicationContext, R.color.actionColor
                                )
                            }
                            if (btnFillColor != null) {
                                addItemSaleSaveButton.doneLoadingAnimation(
                                    btnFillColor,
                                    BitmapFactory.decodeResource(
                                        resources,
                                        R.drawable.ic_done_white_48dp
                                    )
                                )
                            }
                            //Navigate back, with delay for animation to be fully experienced
                            Handler(Looper.getMainLooper()).postDelayed({
                                view.findNavController().popBackStack()
                            }, 600)
                        }
                        is Resource.Failure -> {
                            //End Animation
                            addItemSaleSaveButton.revertAnimation()
                            addItemSaleSaveButton.background = ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.btn_background_blue
                            )
                            //Display Snackbar, depending on the error
                            val exception: Exception = it.throwable as Exception
                            if (exception is InvalidSalePurchaseBalanceException) {
                                Snackbar.make(
                                    requireView(), exception.message!!, Snackbar.LENGTH_LONG
                                ).show()
                            }
                            if (exception is DatabaseException) {
                                Snackbar.make(
                                    requireView(), exception.message!!, Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }


}