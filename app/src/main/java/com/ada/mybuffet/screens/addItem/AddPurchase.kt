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
import com.ada.mybuffet.utils.*
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * @author Paul Pfisterer
 * View for the Add Purchase Screen
 */
class AddPurchase : Fragment(R.layout.fragment_add_item) {
    //Arguments passed via the navigation, includes the shareItem
    private val args: AddPurchaseArgs by navArgs()

    //Lazy creation of the View model via the Factory with the required arguments
    private val viewModel: AddItemViewModel by viewModels() {
        AddItemViewModelFactory(
            AddItemRepository(),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Argument. If the view was opened from the start page, the shareItem is null
        val shareItem = args.shareItem

        //Setup binding
        val binding = FragmentAddItemBinding.bind(view)

        binding.apply {
            //Set Symbol and name if passed in the argument
            if (shareItem != null) {
                addItemPurchaseInputSymbol.setText(shareItem.stockSymbol)
                addItemPurchaseInputSymbol.inputType = InputType.TYPE_NULL
                addItemPurchaseInputSymbol.setTextColor(Color.GRAY)
                addItemPurchaseInputName.setText(shareItem.stockName)
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

    /**
     * This function is invoked, when the form is submitted
     * The input is validated.
     * If everythings correct, call the onFormSubmitted method of the ViewModel
     * If not, display a Snackbar with an error message
     */
    private fun handleSubmit(
        binding: FragmentAddItemBinding,
        viewModel: AddItemViewModel,
        shareItemVal: ShareItem?,
        view: View
    ) {
        binding.apply {
            //Get the input
            var shareItem = shareItemVal
            val symbol = addItemPurchaseInputSymbol.text.toString()
            val name = addItemPurchaseInputName.text.toString()
            val price = addItemPurchaseInputPrice.text.toString()
            val numberString = addItemPurchaseInputNumber.text.toString()
            val fees = addItemPurchaseInputFees.text.toString()
            //Check if everything is filled out
            if (symbol.isNotEmpty()
                && name.isNotEmpty()
                && price.isNotEmpty()
                && numberString.isNotEmpty()
                && fees.isNotEmpty()
            ) {
                //Prevent 0 as input for values
                if(price.toDouble() == 0.0 || numberString.toDouble() == 0.0) {
                    Snackbar.make(
                        requireView(),
                        "Zero as value is not accepted",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return
                }

                //Valid Input, start Animation, Create purchaseItem
                addItemPurchaseSaveButton.startAnimation()
                val number = numberString.toString().toInt()
                val purchaseItem = Purchase(
                    date = Date(),
                    fees = fees,
                    sharePrice = price,
                    shareNumber = number
                )
                //Create shareItem, where the id is set to null
                if (shareItem == null) {
                    shareItem = ShareItem(
                        stockName = name,
                        stockSymbol = symbol
                    )
                }
                //Call the method of the view model with the purchaseItem and the shareItem
                //The shareItems id is null -> this will invoke the createPurchaseWithoutId method
                viewModel.onFormSubmitted(purchaseItem, shareItem).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            //Animate Success
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
                            //Navigate back, with delay for animation to be fully experienced
                            Handler(Looper.getMainLooper()).postDelayed({
                                view.findNavController().popBackStack()
                            }, 600)
                        }
                        is Resource.Failure -> {
                            //End Animation
                            addItemPurchaseSaveButton.revertAnimation()
                            addItemPurchaseSaveButton.background = ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.btn_background_blue
                            )
                            //Display Snackbar, depending on the error
                            val exception: Exception = it.throwable as Exception
                            if (exception is StockNotFound) {
                                Snackbar.make(
                                    requireView(), exception.message!!, Snackbar.LENGTH_LONG
                                ).show()
                            }
                            if (exception is DatabaseException) {
                                Snackbar.make(
                                    requireView(), exception.message!!, Snackbar.LENGTH_LONG
                                ).show()
                            }
                            if (exception is StockRestrictedAccess) {
                                Snackbar.make(
                                    requireView(), exception.message!!, Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            } else {
                //Invalid Input, Display error snackbar
                Snackbar.make(
                    requireView(),
                    "All Fields have to be filled",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}