package com.ada.mybuffet.screens.stocks

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView

import com.ada.mybuffet.databinding.RecyclerViewItemStockshareBinding
import com.ada.mybuffet.repo.StockShare


class StocksListAdapter :  ListAdapter<StockShare, StocksListAdapter.StockDetailViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDetailViewHolder {
        val binding = RecyclerViewItemStockshareBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockDetailViewHolder(binding)
    }


    class StockDetailViewHolder(private val binding: RecyclerViewItemStockshareBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(share: StockShare) {
            binding.apply {
                recyclerStockItemTvStockSymbol.text = share.symbol
                recyclerStockItemTvStockName.text = share.name
                recyclerStockItemTvDividendTotal.text = share.getLastDividend().toString()
                recyclerStockItemTvDividendPercent.text = "${share.getPercentDividend()} %"
                recyclerStockItemTvStockPrice.text = share.curPrice.toString()
                recyclerStockItemTvStockPricePercent.text = "(${share.getPercentStockPrice()} %)"
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