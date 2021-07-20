package com.ada.mybuffet.screens.addItem

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
import com.ada.mybuffet.databinding.FragmentAddDividendBinding
import com.ada.mybuffet.databinding.FragmentAddSaleBinding
import com.ada.mybuffet.screens.addItem.repo.AddItemRepository
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModel
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModelFactory
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.DatabaseException
import com.ada.mybuffet.utils.Resource
import com.ada.mybuffet.utils.StockNotFound
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * @author Paul Pfisterer
 * View for the Add Dividend Screen
 */
class AddDividend : Fragment(R.layout.fragment_add_dividend) {
    //Arguments passed via the navigation, includes the shareItem
    private val args: AddDividendArgs by navArgs()
    //Lazy creation of the View model via the Factory with the required arguments
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
        val binding = FragmentAddDividendBinding.bind(view)

        binding.apply {
            //Set on click listeners
            addItemDividendClose.setOnClickListener {
                view.findNavController().popBackStack()
            }

            addItemDividendSaveButton.setOnClickListener {
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
        binding: FragmentAddDividendBinding,
        viewModel: AddItemViewModel,
        shareItem: ShareItem,
        view: View
    ) {
        binding.apply {
            //Get the input
            val amount = addItemDividendInputAmount.text.toString()
            val numberString = addItemDividendInputNumber.text.toString()
            //Check if everything is filled out
            if (amount.isNotEmpty()
                && numberString.isNotEmpty()
            ) {
                //Valid Input, start Animation, Create dividendItem
                addItemDividendSaveButton.startAnimation()
                val number = numberString.toString().toInt()
                val dividendItem = DividendItem(
                    date = Date(),
                    amount = amount,
                    number = number
                )
                //Call the method of the view model with the dividendItem and the shareItem
                viewModel.onFormSubmitted(dividendItem, shareItem).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            //Animate Success
                            val btnFillColor = activity?.let { it1 ->
                                ContextCompat.getColor(
                                    it1.applicationContext, R.color.actionColor)
                            }
                            if (btnFillColor != null) {
                                addItemDividendSaveButton.doneLoadingAnimation(btnFillColor, BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                            }
                            //Navigate back, with delay for animation to be fully experienced
                            Handler(Looper.getMainLooper()).postDelayed({
                                view.findNavController().popBackStack()
                            }, 600)
                        }
                        is Resource.Failure -> {
                            //End Animation
                            addItemDividendSaveButton.revertAnimation()
                            addItemDividendSaveButton.background = ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.btn_background_blue
                            )
                            //Display Snackbar, depending on the error
                            val exception: Exception = it.throwable as Exception
                            if (exception is DatabaseException) {
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