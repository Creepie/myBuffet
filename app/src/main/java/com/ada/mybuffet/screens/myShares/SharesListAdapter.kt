package com.ada.mybuffet.screens.myShares

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.RecyclerViewItemMysharesStocksBinding
import com.ada.mybuffet.utils.NumberFormatUtils

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
                binding.recyclerViewTvStockPrice.text = NumberFormatUtils.toCurrencyString(currentPrice)
                binding.recyclerViewTvStockPricePercent.text = NumberFormatUtils.toPercentString(currentPricePercent)
                binding.recyclerViewTvDividendTotal.text = NumberFormatUtils.toCurrencyString(totalDividends)
                binding.recyclerViewTvTotalHoldingsValue.text = NumberFormatUtils.toCurrencyString(totalHoldings)

                if (isPricePositive()) {
                    binding.recyclerViewImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_up)
                    binding.recyclerViewImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_up)

                    val textColor = ContextCompat.getColor(holder.binding.root.context, R.color.sharePrice_profit)
                    binding.recyclerViewTvStockPrice.setTextColor(textColor)
                    binding.recyclerViewTvStockPricePercent.setTextColor(textColor)
                } else {
                    binding.recyclerViewImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_down)
                    binding.recyclerViewImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_down)

                    val textColor = ContextCompat.getColor(holder.binding.root.context, R.color.sharePrice_loss)
                    binding.recyclerViewTvStockPrice.setTextColor(textColor)
                    binding.recyclerViewTvStockPricePercent.setTextColor(textColor)
                }

                if (isInvestmentPositive()) {
                    val textColor = ContextCompat.getColor(holder.binding.root.context, R.color.sharePrice_profit)
                    binding.recyclerViewTvTotalHoldingsValue.setTextColor(textColor)
                } else {
                    val textColor = ContextCompat.getColor(holder.binding.root.context, R.color.sharePrice_loss)
                    binding.recyclerViewTvTotalHoldingsValue.setTextColor(textColor)
                }
            }
        }
    }

    override fun getItemCount() = sharesList.size

    inner class SharesListViewHolder(val binding: RecyclerViewItemMysharesStocksBinding) : RecyclerView.ViewHolder(binding.root)

}