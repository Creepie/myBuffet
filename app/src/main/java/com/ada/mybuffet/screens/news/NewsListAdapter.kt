package com.ada.mybuffet.screens.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.RecyclerItemNewsBinding
import com.ada.mybuffet.databinding.RecyclerViewItemStockshareBinding
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse


class NewsListAdapter :  ListAdapter<SymbolPressResponse, NewsListAdapter.StockDetailViewHolder>(
        DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDetailViewHolder {
            val binding = RecyclerItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return StockDetailViewHolder(binding)
        }


        class StockDetailViewHolder(private val binding: RecyclerItemNewsBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(share: SymbolPressResponse) {
                binding.apply {

                    for (i in share.majorDevelopment.indices){
                        recyclerItemHeader.text = share.majorDevelopment.get(i).headline
                        recyclerItemSub.text = share.majorDevelopment.get(i).symbol
                    }

                }
            }
        }


        class DiffCallback : DiffUtil.ItemCallback<SymbolPressResponse>() {

            override fun areItemsTheSame(
                oldItem: SymbolPressResponse,
                newItem: SymbolPressResponse
            ): Boolean = oldItem.symbol == newItem.symbol

            override fun areContentsTheSame(
                oldItem: SymbolPressResponse,
                newItem: SymbolPressResponse
            ): Boolean = oldItem == newItem
        }


        override fun onBindViewHolder(holder: StockDetailViewHolder, position: Int) {
            val curItem = getItem(position)
            holder.bind(curItem)
        }
}