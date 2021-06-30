package com.ada.mybuffet.screens.detailShare

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentShareDetailBinding
import com.ada.mybuffet.screens.detailShare.model.DividendItem
import com.ada.mybuffet.screens.detailShare.model.FeeItem
import com.ada.mybuffet.screens.detailShare.model.Purchase
import com.ada.mybuffet.screens.detailShare.model.SaleItem
import com.ada.mybuffet.screens.detailShare.repo.ShareDetailRepository
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModel
import com.ada.mybuffet.screens.detailShare.viewModel.ShareDetailViewModelFactory
import com.ada.mybuffet.screens.myShares.MySharesDirections
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.utils.Resource
import com.google.android.material.snackbar.Snackbar

class ShareDetail : Fragment(R.layout.fragment_share_detail) {

    private val args: ShareDetailArgs by navArgs()

    private val viewModel: ShareDetailViewModel by viewModels() {
        ShareDetailViewModelFactory(
            ShareDetailRepository(),
            args.shareItem.shareItemId
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
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(shareDetailRecyclerViewDividends)
            ItemTouchHelper(simpleTouchHandler).attachToRecyclerView(shareDetailRecyclerViewFees)

        }

        //Observe View Model
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

        viewModel.fetchDividendItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val dividends: List<DividendItem> = (it.data as List<DividendItem>)
                    shareDetailDividendAdapter.submitList(dividends)
                    if (dividends.isEmpty()) {
                        binding.shareDetailRecyclerViewDividendsEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.shareDetailRecyclerViewDividendsEmptyMessage.visibility = View.GONE
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

            shareDetailRefreshButton.setOnClickListener {
                it.animate().rotationBy(360f)
            }

            shareDetailFabAddPurchase.setOnClickListener{
                val action = ShareDetailDirections.actionShareDetailToAddItem(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddSale.setOnClickListener{
                val action = ShareDetailDirections.actionShareDetailToAddSale(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddFees.setOnClickListener{
                val action = ShareDetailDirections.actionShareDetailToAddFee(shareItem)
                findNavController().navigate(action)
            }

            shareDetailFabAddDividends.setOnClickListener{
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


}