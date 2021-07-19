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
import com.ada.mybuffet.utils.StockCalculationUtils


/**
 * @author Paul Pfisterer
 * View for the Detail Screen
 */
class ShareDetail : Fragment(R.layout.fragment_share_detail) {
    //Arguments passed via the navigation, includes the shareItem
    private val args: ShareDetailArgs by navArgs()

    //Lazy creation of the View model via the Factory with the required arguments
    private val viewModel: ShareDetailViewModel by viewModels() {
        ShareDetailViewModelFactory(
            ShareDetailRepository(),
            args.shareItem
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get the shareItem (Representing the current Stock) from the passed arguments
        val shareItem = args.shareItem

        //Setup binding
        val binding = FragmentShareDetailBinding.bind(view)

        //Setup Adapters for the RecyclerView
        val shareDetailPurchaseAdapter = ShareDetailPurchaseAdapter()
        val shareDetailSaleAdapter = ShareDetailSaleAdapter()
        val shareDetailFeeAdapter = ShareDetailFeeAdapter()
        val shareDetailDividendAdapter = ShareDetailDividendAdapter()

        //Setup Recycler Views
        binding.apply {
            //Purchases Recycler
            shareDetailRecyclerViewPurchase.apply {
                adapter = shareDetailPurchaseAdapter
                layoutManager = LinearLayoutManager(requireContext())
                //add seperators
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
                //add seperators
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
                //add seperators
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
                //add seperators
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

            //Create the Swipe to delete touch handler, used by all recycler views
            val simpleTouchHandler = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false;
                }

                //Function that is invoked, when an item is swiped from one of the recycler views
                //Depending on the kind of the viewHolder, the item and the text for
                // the snackBar is gotten from the matching adapter
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
                    //Call and observe the onItemSwiped method of the viewModel
                    viewModel.onItemSwiped(item).observe(viewLifecycleOwner) {
                        when (it) {
                            //On success, show a snackbar with the option to undo the deletion
                            is Resource.Success -> {
                                val deletedItem = it.data
                                Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
                                    .setAction("UNDO") {
                                        //If undo is clicked, call and observe the onUndoDeleteItem
                                        // method of the viewModel
                                        viewModel.onUndoDeleteItem(deletedItem)
                                            .observe(viewLifecycleOwner) {}
                                    }.show()
                            }
                            //On failure, display snackbar, and undo swipping by refreshing
                            is Resource.Failure -> {
                                if (item is Purchase) {
                                    val failureText = R.string.share_detail_cant_be_deleted
                                    shareDetailPurchaseAdapter.notifyItemChanged(viewHolder.adapterPosition)
                                    Snackbar.make(
                                        requireView(), failureText, Snackbar.LENGTH_LONG
                                    ).show()
                                }
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

        //Observe Purchase List. Set List of the adapter and handle empty lists
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

        //Observe Sale list. Set List of the adapter and handle empty lists
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

        //Observe Fee List. Set List of the adapter and handle empty lists
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

        //Observe Devidends List. Set List of the adapter and handle empty lists
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

        //Observe Overview Data-Changes. Whenever the returned overview data object changes,
        //the values of the detail page are updated
        viewModel.overviewData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data

                    //Spinner method is called, spins as long, as the price is not returned
                    spinButton(data, binding.shareDetailRefreshButton)

                    //stock price
                    val stockPrice = data.currentWorth
                    binding.shareDetailStockPrice.text = String.format("€ %.2f", stockPrice)

                    //stock price percentage
                    val stockPricePercentage = data.currentWorthPercentage
                    var pricePercentageText = if (stockPricePercentage < 0) {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                        binding.shareDetailStockPricePercentage.setTextColor(textColor)
                        "("
                    } else {
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                        binding.shareDetailStockPricePercentage.setTextColor(textColor)
                        "(+"
                    }
                    pricePercentageText += String.format("%.2f%%)", stockPricePercentage)
                    binding.shareDetailStockPricePercentage.text = pricePercentageText

                    //TotalFees
                    val totalFees = data.feeSum + data.purchaseFeeSum + data.saleFeeSum
                    binding.shareDetailFeesValue.text = String.format("€ %.2f", totalFees)

                    //Total Sales
                    val totalSales = data.saleSum
                    binding.shareDetailTotalSalesPlaceholder.text =
                        String.format("€ %.2f", totalSales)

                    //Dividend
                    binding.shareDetailDividendProfit.text =
                        String.format("€ %.2f", data.dividendSum)

                    //Investment Sum
                    val investmentSum =
                        data.feeSum + data.purchaseFeeSum + data.saleFeeSum + data.purchaseSum
                    binding.shareDetailInvestmentSum.text = String.format("€ %.2f", investmentSum)

                    //Holdings Value Change + Total Holdings
                    var totalHoldings = data.currentWorth * (data.purchaseCount - data.saleCount)
                    if (totalHoldings < 0) {
                        totalHoldings = 0.0
                    }
                    val exchangeBilance = StockCalculationUtils.calculateExchangeBalance(data)
                    binding.shareDetailTotalHoldingsPlaceholder.text =
                        String.format("€ %.2f", totalHoldings)
                    if (exchangeBilance < 0.0) {
                        binding.shareDetailProfit.text =
                            String.format("- € %.2f", exchangeBilance * (-1))
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                        binding.shareDetailProfit.setTextColor(textColor)
                        binding.shareDetailExchangeTrend.setImageResource(R.drawable.ic_trending_down)
                        binding.shareDetailTotalHoldingsPlaceholder.setTextColor(textColor)
                    } else {
                        binding.shareDetailProfit.text = String.format("+ € %.2f", exchangeBilance)
                        val textColor =
                            ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                        binding.shareDetailProfit.setTextColor(textColor)
                        binding.shareDetailTotalHoldingsPlaceholder.setTextColor(textColor)
                        binding.shareDetailExchangeTrend.setImageResource(R.drawable.ic_trending_up)
                    }

                    //Profit
                    val valueIncrease = StockCalculationUtils.calculateProfit(data)
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

                    //Profit percentage increase
                    val percentage =
                        StockCalculationUtils.getProfitPercentage(investmentSum, valueIncrease)
                    var percentageText = if (percentage < 0) {
                        "("
                    } else {
                        "(+"
                    }
                    percentageText += String.format("%.2f%%)", percentage)
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

        //Set name of stock
        binding.apply {
            shareDetailStockSign.text = shareItem.stockSymbol
        }

        //Set click listeners
        binding.apply {
            //Back Button
            shareDetailBackButton.setOnClickListener {
                view.findNavController().popBackStack()
            }
            //Action Button
            shareDetailFabAdd.setOnClickListener {
                if (View.GONE == binding.shareViewFabLayout1.visibility) {
                    showFABMenu(binding)
                } else {
                    hideFABMenu(binding)
                }
            }
            //Refresh Button
            shareDetailRefreshButton.setOnClickListener {
                it.animate().rotationBy(360f)
            }
            //All sub action buttons. Navigate to a new page with the shareItem as argument
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

    /**
     * Handle transitions for showing sub-action buttons when clicking on the action button
     */
    private fun showFABMenu(binding: FragmentShareDetailBinding) {
        binding.shareViewFabLayout1.visibility = View.VISIBLE
        binding.shareViewFabLayout2.visibility = View.VISIBLE
        binding.shareViewFabLayout3.visibility = View.VISIBLE
        binding.shareViewFabLayout4.visibility = View.VISIBLE
        binding.shareDetailFabAdd.animate().rotationBy(135F)
        binding.shareViewFabLayout1.animate().translationY(-200f)
        binding.shareViewFabLayout2.animate().translationY(-350f)
        binding.shareViewFabLayout3.animate().translationY(-500f)
        binding.shareViewFabLayout4.animate().translationY(-650f)

    }

    /**
     * Handle transitions for hiding sub-action buttons when clicking again on the action button
     */
    private fun hideFABMenu(binding: FragmentShareDetailBinding) {
        binding.shareViewFabLayout1.visibility = View.GONE
        binding.shareViewFabLayout2.visibility = View.GONE
        binding.shareViewFabLayout3.visibility = View.GONE
        binding.shareViewFabLayout4.visibility = View.GONE
        binding.shareDetailFabAdd.animate().rotationBy(-135F)
    }

    /**
     * Handles the rotation animation.
     * The rotation duration is ended, when the data contains a value for the price of the stock
     */
    private fun spinButton(data: OverviewData, button: ImageButton) {
        var rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitly)
        if (data.currentWorth == 0.0) {
            button.startAnimation(rotation)
        } else {
            button.clearAnimation()
        }


    }


}