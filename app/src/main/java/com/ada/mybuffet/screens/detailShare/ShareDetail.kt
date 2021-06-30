package com.ada.mybuffet.screens.detailShare

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentShareDetailBinding
import com.ada.mybuffet.screens.detailShare.model.*
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModel
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModelFactory
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar

import android.view.animation.RotateAnimation
import android.widget.ImageButton


class ShareDetail : Fragment(R.layout.fragment_share_detail) {

    private val args: ShareDetailArgs by navArgs()

    private val viewModel: ShareDetailViewModel by viewModels() {
        ShareDetailViewModelFactory(
            ShareDetailRepository(),
            args.shareItem
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Argument
        val shareItem = args.shareItem

        //Setup binding
        val binding = FragmentShareDetailBinding.bind(view)

        //Setup RecyclerView
        val shareDetailPurchaseAdapter = ShareDetailPurchaseAdapter()
        val shareDetailSaleAdapter = ShareDetailSaleAdapter()
        val shareDetailFeeAdapter = ShareDetailFeeAdapter()
        val shareDetailDividendAdapter = ShareDetailDividendAdapter()


        binding.apply {
            //Purchases Recycler
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

            //Sales Recycler
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

            //Fees Recycler
            shareDetailRecyclerViewFees.apply {
                adapter = shareDetailFeeAdapter
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

            //Dividends Recycler
            shareDetailRecyclerViewDividends.apply {
                adapter = shareDetailDividendAdapter
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

            //Delete To Swipe TouchHandler
            val simpleTouchHandler = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false;
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val item = when (viewHolder) {
                        is ShareDetailSaleAdapter.ShareDetailViewHolder -> shareDetailSaleAdapter.currentList[viewHolder.adapterPosition]
                        is ShareDetailPurchaseAdapter.ShareDetailViewHolder -> shareDetailPurchaseAdapter.currentList[viewHolder.adapterPosition]
                        is ShareDetailFeeAdapter.ShareDetailViewHolder -> shareDetailFeeAdapter.currentList[viewHolder.adapterPosition]
                        is ShareDetailDividendAdapter.ShareDetailViewHolder -> shareDetailDividendAdapter.currentList[viewHolder.adapterPosition]
                        else -> return
                    }
                    val text = when (viewHolder) {
                        is ShareDetailSaleAdapter.ShareDetailViewHolder -> R.string.share_detail_sale_deleted
                        is ShareDetailPurchaseAdapter.ShareDetailViewHolder -> R.string.share_detail_purchase_deleted
                        is ShareDetailFeeAdapter.ShareDetailViewHolder -> R.string.share_detail_fee_deleted
                        is ShareDetailDividendAdapter.ShareDetailViewHolder -> R.string.share_detail_dividend_deleted
                        else -> return
                    }
                    viewModel.onItemSwiped(item).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                val deletedItem = it.data
                                Snackbar.make(
                                    requireView(),
                                    text,
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("UNDO") {
                                        viewModel.onUndoDeleteItem(deletedItem)
                                            .observe(viewLifecycleOwner) {}
                                    }
                                    .show()
                            }
                        }
                    }

                }
            }
            //Subscribe to touch handler
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(shareDetailRecyclerViewPurchase)
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(shareDetailRecyclerViewSales)
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(
                shareDetailRecyclerViewDividends
            )
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(shareDetailRecyclerViewFees)

        }

        //Observe Purchase List
        viewModel.fetchPurchaseItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val purchases: List<Purchase> = (it.data as List<Purchase>)
                    shareDetailPurchaseAdapter.submitList(purchases)
                    if (purchases.isEmpty()) {
                        binding.shareDetailRecyclerViewPurchaseEmptyMessage.visibility =
                            View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewPurchaseEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        //Observe Sale list
        viewModel.fetchSaleItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val sales: List<SaleItem> = (it.data as List<SaleItem>)
                    shareDetailSaleAdapter.submitList(sales)
                    if (sales.isEmpty()) {
                        binding.shareDetailRecyclerViewSalesEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewSalesEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        //Observe Fee List
        viewModel.fetchFeeItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val fees: List<FeeItem> = (it.data as List<FeeItem>)
                    shareDetailFeeAdapter.submitList(fees)
                    if (fees.isEmpty()) {
                        binding.shareDetailRecyclerViewFeesEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewFeesEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        //Observe Devidends List
        viewModel.fetchDividendItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val dividends: List<DividendItem> = (it.data as List<DividendItem>)
                    shareDetailDividendAdapter.submitList(dividends)
                    if (dividends.isEmpty()) {
                        binding.shareDetailRecyclerViewDividendsEmptyMessage.visibility =
                            View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewDividendsEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }

        //Observe Overview Data-Changes
        viewModel.overviewData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data

                    //Spinner
                    spinButton(data, binding.shareDetailRefreshButton)

                    //TotalFees
                    val totalFees = data.feeSum + data.purchaseFeeSum + data.saleFeeSum
                    binding.shareDetailFeesValue.text = String.format("€ %.2f", totalFees)

                    //Dividend
                    binding.shareDetailDividendProfit.text =
                        String.format("€ %.2f", data.dividendSum)

                    //Investment Sum
                    val investmentSum =
                        data.feeSum + data.purchaseFeeSum + data.saleFeeSum + data.purchaseSum
                    binding.shareDetailInvestmentSum.text = String.format("€ %.2f", investmentSum)

                    //ExchangeBilance
                    val exchangeBilance = calculateExchangeBalance(data)
                    binding.shareDetailProfit.text = String.format("€ %.2f", exchangeBilance)
                    if (exchangeBilance < 0.0) {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                        binding.shareDetailProfit.setTextColor(textColor)
                        binding.shareDetailExchangeTrend.setImageResource(R.drawable.ic_trending_down)
                    } else {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                        binding.shareDetailProfit.setTextColor(textColor)
                        binding.shareDetailExchangeTrend.setImageResource(R.drawable.ic_trending_up)
                    }

                    //Value increase
                    val valueIncrease = calculateProfit(data)
                    binding.shareDetailStockValue.text = String.format("€ %.2f", valueIncrease)
                    if (valueIncrease < 0.0) {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                        binding.shareDetailStockValue.setTextColor(textColor)
                        binding.shareDetailTotalTrending.setImageResource(R.drawable.ic_trending_down)
                    } else {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                        binding.shareDetailStockValue.setTextColor(textColor)
                        binding.shareDetailTotalTrending.setImageResource(R.drawable.ic_trending_up)
                    }

                    //Value increse percentage
                    val percentage = getPercentage(investmentSum, valueIncrease)
                    var percentageText = if (percentage < 0) {
                        "("
                    } else {
                        "(+"
                    }
                    percentageText += String.format("%.2f)", percentage)
                    binding.shareDetailStockPercentage.text = percentageText
                    if (percentage < 0.0) {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                        binding.shareDetailStockPercentage.setTextColor(textColor)
                    } else {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                        binding.shareDetailStockPercentage.setTextColor(textColor)
                    }
                }
            }
        }

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

            shareDetailRefreshButton.setOnClickListener {
                it.animate().rotationBy(360f)
            }

            shareDetailFabAddPurchase.setOnClickListener {
                val action = ShareDetailDirections.actionShareDetailToAddItem(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddSale.setOnClickListener {
                val action = ShareDetailDirections.actionShareDetailToAddSale(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddFees.setOnClickListener {
                val action = ShareDetailDirections.actionShareDetailToAddFee(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddDividends.setOnClickListener {
                val action = ShareDetailDirections.actionShareDetailToAddDividend(shareItem)
                findNavController().navigate(action)
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
    }


    private fun calculateProfit(
        data: OverviewData,
    ): Double {
        //If no current Worth is passed, return
        if (data.currentWorth == 0.0) {
            return 0.0
        }

        //Calculate how much money is still in the shares
        //Can be negative, if the sales are bigger than the purchases -> in this case
        //they are added not subtracted later on
        val currentMoneyInShares = data.purchaseSum - data.saleSum

        //Calculate how many shares are still owned
        val currentAmountOfShares = data.purchaseCount - data.saleCount
        if (currentAmountOfShares < 0) {
            return 0.0
        }

        //Calculate the worth of the shares owned with the current price
        val currentSharesWorth = currentAmountOfShares * data.currentWorth
        //Subtract the money in the shares from the shares worth to get the profit
        val currentSharesProfit = currentSharesWorth - currentMoneyInShares


        return currentSharesProfit + data.dividendSum - data.saleFeeSum - data.feeSum - data.purchaseFeeSum
    }


    private fun calculateExchangeBalance(
        data: OverviewData
    ): Double {
        if (data.currentWorth == 0.0) {
            return 0.0
        }

        val pricePerPurchase = if (data.purchaseCount != 0) {
            data.purchaseSum / data.purchaseCount
        } else {
            0.0
        }

        val stockHoldingAmount = data.purchaseCount - data.saleCount
        if (stockHoldingAmount < 0) {
            return 0.0
        }

        val oldHoldingsWorth = stockHoldingAmount * pricePerPurchase
        val currentHoldingWorth = stockHoldingAmount * data.currentWorth
        return currentHoldingWorth - oldHoldingsWorth
    }

    private fun getPercentage(investmentSum: Double, valueIncrease: Double): Double {
        return if (valueIncrease == 0.0 || investmentSum == 0.0) {
            0.0
        } else {
            (valueIncrease / investmentSum) * 100
        }
    }

    private fun spinButton(data: OverviewData, button: ImageButton) {
        var rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitly)
        if (data.currentWorth == 0.0) {
            button.startAnimation(rotation)
        } else {
            button.clearAnimation()
        }


    }


}