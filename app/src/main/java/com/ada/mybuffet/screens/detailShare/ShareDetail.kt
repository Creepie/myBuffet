package com.ada.mybuffet.screens.detailShare

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentShareDetailBinding
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModel
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModelFactory
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource

class ShareDetail : Fragment(R.layout.fragment_share_detail) {

    private val viewModel: ShareDetailViewModel by viewModels() {
        ShareDetailViewModelFactory(
            ShareDetailRepository()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO replace
        val shareItem = ShareItem(
            stockSymbol = "VOE.VI",
            stockName = "Voestalpine AG",
            currentPrice = "33.12",
            currentPricePercent = "11.74",
            totalDividends = "113.17",
            totalHoldings = "631.15",
            totalFees = "13.75",
            totalInvestment = "600.00"
        )

        val binding = FragmentShareDetailBinding.bind(view)

        //Setup Recycler
        val shareDetailPurchaseAdapter = ShareDetailPurchaseAdapter()
        val shareDetailSaleAdapter = ShareDetailSaleAdapter()


        binding.apply {
            shareDetailRecyclerViewPurchase.apply {
                adapter = shareDetailPurchaseAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    ).also { deco ->
                        with(ShapeDrawable(RectShape())) {
                            intrinsicHeight = (resources.displayMetrics.density * 8).toInt()
                            alpha = 0
                            deco.setDrawable(this)
                        }
                    })
            }

            shareDetailRecyclerViewSales.apply {
                adapter = shareDetailSaleAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    ).also { deco ->
                        with(ShapeDrawable(RectShape())) {
                            intrinsicHeight = (resources.displayMetrics.density * 8).toInt()
                            alpha = 0
                            deco.setDrawable(this)
                        }
                    })
            }
        }

        //Observe View Model
        viewModel.fetchPurchaseItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val purchases: List<Purchase> = (it.data as List<Purchase>)
                    shareDetailPurchaseAdapter.submitList(purchases)
                    if(purchases.isEmpty()) {
                        binding.shareDetailRecyclerViewPurchaseEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewPurchaseEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.fetchSaleItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val sales: List<SaleItem> = (it.data as List<SaleItem>)
                    shareDetailSaleAdapter.submitList(sales)
                    if(sales.isEmpty()) {
                        binding.shareDetailRecyclerViewSalesEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewSalesEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        //TODO is it in the right place?
        //Set text
        binding.apply {
            shareDetailStockSign.text = shareItem.stockSymbol
        }

        //React to  Clicks
        binding.apply {
            shareDetailBackButton.setOnClickListener {
                view.findNavController().popBackStack()
            }

            shareDetailFabAdd.setOnClickListener {
                if (View.GONE == binding.shareViewFabLayout1.visibility) {
                    showFABMenu(binding)
                } else {
                    hideFABMenu(binding)
                }
            }
        }
    }

    private fun showFABMenu(binding: FragmentShareDetailBinding) {
        binding.shareViewFabLayout1.visibility = View.VISIBLE
        binding.shareViewFabLayout2.visibility = View.VISIBLE
        binding.shareViewFabLayout3.visibility = View.VISIBLE
        binding.shareViewFabLayout4.visibility = View.VISIBLE
        binding.shareDetailFabAdd.animate().rotationBy(180F)
        binding.shareViewFabLayout1.animate().translationY(-200f)
        binding.shareViewFabLayout2.animate().translationY(-350f)
        binding.shareViewFabLayout3.animate().translationY(-500f)
        binding.shareViewFabLayout4.animate().translationY(-650f)

    }

    private fun hideFABMenu(binding: FragmentShareDetailBinding) {
        binding.shareViewFabLayout1.visibility = View.GONE
        binding.shareViewFabLayout2.visibility = View.GONE
        binding.shareViewFabLayout3.visibility = View.GONE
        binding.shareViewFabLayout4.visibility = View.GONE
        binding.shareDetailFabAdd.animate().rotationBy(-180F)
        //binding.shareViewFabLayout1.animate().translationY(200f)
    }


}