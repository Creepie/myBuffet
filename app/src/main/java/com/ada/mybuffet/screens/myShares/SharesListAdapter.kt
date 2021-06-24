package com.ada.mybuffet.screens.myShares

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemMysharesStocksBinding

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
            }
        }
    }

    override fun getItemCount() = sharesList.size

    inner class SharesListViewHolder(val binding: RecyclerViewItemMysharesStocksBinding) : RecyclerView.ViewHolder(binding.root)

}