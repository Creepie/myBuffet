package com.ada.mybuffet.screens.stocks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.R

import com.ada.mybuffet.databinding.RecyclerViewItemStockshareBinding
import com.ada.mybuffet.repo.StockShare


class StocksListAdapter(
   private val listener: StocksRecyclerViewClickListener
) :  ListAdapter<StockShare, StocksListAdapter.StockDetailViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDetailViewHolder {
        val binding = RecyclerViewItemStockshareBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockDetailViewHolder(binding,listener)
    }




    class StockDetailViewHolder(private val binding: RecyclerViewItemStockshareBinding,
    private val listener: StocksRecyclerViewClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(share: StockShare) {
            binding.apply {
                recyclerStockItemTvStockSymbol.text = share.symbol
                recyclerStockItemTvStockName.text = share.name
                recyclerStockItemTvDividendTotal.text = share.getLastDividend().toString()
                recyclerStockItemTvDividendPercent.text = "${share.getPercentDividend()} %"
                recyclerStockItemTvStockPrice.text = share.curPrice.toString()
                recyclerStockItemTvStockPricePercent.text = "(${share.getPercentStockPrice()} %)"

                if (share.isPricePositive()){
                    recyclerStockItemImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_up)
                    val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                    recyclerStockItemTvStockPricePercent.setTextColor(textColor)
                } else {
                    recyclerStockItemImgSharePriceUpDown.setImageResource(R.drawable.ic_trending_down)
                    val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                    recyclerStockItemTvStockPricePercent.setTextColor(textColor)
                }

                if (share.isDividendIncreasing()){
                    recyclerStockItemImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_up)
                    val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                    recyclerStockItemTvDividendPercent.setTextColor(textColor)
                } else {
                    recyclerStockItemImgTotalHoldingsUpDown.setImageResource(R.drawable.ic_trending_down)
                    val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                    recyclerStockItemTvDividendPercent.setTextColor(textColor)
                }
                root.setOnClickListener {
                    listener.onCardClicked(share)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StockShare>() {
        override fun areItemsTheSame(oldItem: StockShare, newItem: StockShare): Boolean =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: StockShare, newItem: StockShare): Boolean =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: StockDetailViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }

}