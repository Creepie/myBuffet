package com.ada.mybuffet.screens.myShares

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemMysharesStocksBinding

class StocksListAdapter(private val stocksList: ArrayList<StocksListItem>) :
    RecyclerView.Adapter<StocksListAdapter.StocksListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksListViewHolder {
        val binding = RecyclerViewItemMysharesStocksBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return StocksListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StocksListViewHolder, position: Int) {
        with(holder) {
            with(stocksList[position]) {
                binding.rclviewTvStockSymbol.text = stockSymbol
            }
        }
    }

    override fun getItemCount() = stocksList.size

    inner class StocksListViewHolder(val binding: RecyclerViewItemMysharesStocksBinding) : RecyclerView.ViewHolder(binding.root)

}