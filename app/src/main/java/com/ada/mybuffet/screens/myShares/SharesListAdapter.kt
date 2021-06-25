package com.ada.mybuffet.screens.myShares

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.RecyclerViewItemMysharesStocksBinding
import com.ada.mybuffet.utils.CurrencyFormatter
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class SharesListAdapter(private val sharesList: ArrayList<ShareItem>) :
    RecyclerView.Adapter<SharesListAdapter.SharesListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharesListViewHolder {
        val binding = RecyclerViewItemMysharesStocksBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SharesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SharesListViewHolder, position: Int) {
        with(holder) {
            with(sharesList[position]) {
                binding.recyclerViewTvStockSymbol.text = stockSymbol
                binding.recyclerViewTvStockName.text = stockName
                binding.recyclerViewTvStockPrice.text = CurrencyFormatter.toCurrencyString(currentPrice)
                binding.recyclerViewTvStockPricePercent.text = getCurrentPricePercentFormatted()
                binding.recyclerViewTvDividendTotal.text = CurrencyFormatter.toCurrencyString(totalDividends)
                binding.recyclerViewTvTotalHoldingsValue.text = CurrencyFormatter.toCurrencyString(totalHoldings)

                if (isPricePositive()) {
                    binding.recyclerViewImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_up)
                    binding.recyclerViewImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_up)
                } else {
                    binding.recyclerViewImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_down)
                    binding.recyclerViewImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_down)
                }
            }
        }
    }

    override fun getItemCount() = sharesList.size

    inner class SharesListViewHolder(val binding: RecyclerViewItemMysharesStocksBinding) : RecyclerView.ViewHolder(binding.root)

}