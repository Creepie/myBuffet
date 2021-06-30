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
import com.ada.mybuffet.databinding.FragmentAddFeeBinding
import com.ada.mybuffet.databinding.FragmentAddSaleBinding
import com.ada.mybuffet.screens.addItem.repo.AddItemRepository
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModel
import com.ada.mybuffet.screens.addItem.viewModel.AddItemViewModelFactory
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddFee : Fragment(R.layout.fragment_add_fee) {
    private val args: AddFeeArgs by navArgs()
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
        val binding = FragmentAddFeeBinding.bind(view)

        binding.apply {
            //Set on click listeners
            addItemFeeClose.setOnClickListener {
                view.findNavController().popBackStack()
            }

            addItemFeeSaveButton.setOnClickListener {
                handleSubmit(binding, viewModel, shareItem, view)
            }
        }
    }


    private fun handleSubmit(
        binding: FragmentAddFeeBinding,
        viewModel: AddItemViewModel,
        shareItem: ShareItem,
        view: View
    ) {
        binding.apply {
            val amount = addItemFeeInputAmount.text.toString()
            val description = addItemFeeInputDescription.text.toString()

            if (amount.isNotEmpty()
                && description.isNotEmpty()
            ) {
                addItemFeeSaveButton.startAnimation()
                val feeItem = FeeItem(
                    date = Date(),
                    amount = amount,
                    description = description
                )
                viewModel.onFormSubmitted(feeItem, shareItem).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            val btnFillColor = activity?.let { it1 ->
                                ContextCompat.getColor(
                                    it1.applicationContext, R.color.actionColor)
                            }
                            if (btnFillColor != null) {
                                addItemFeeSaveButton.doneLoadingAnimation(btnFillColor, BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                view.findNavController().popBackStack()
                            }, 600)
                        }
                        is Resource.Failure -> {
                            addItemFeeSaveButton.revertAnimation()
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