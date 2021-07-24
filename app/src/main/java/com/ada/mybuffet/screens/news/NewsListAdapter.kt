package com.ada.mybuffet.screens.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerItemNewsBinding
import com.ada.mybuffet.repo.SymbolPressResponse


class NewsListAdapter(
    private val listener: NewsRecyclerViewClickListener
) :  ListAdapter<SymbolPressResponse, NewsListAdapter.StockDetailViewHolder>(
        DiffCallback()) {

    companion object {
        var globalArticle: String = ""
        var globalHeadline: String = ""
        var globalSymbol: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDetailViewHolder {
            val binding = RecyclerItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return StockDetailViewHolder(binding, listener)
        }



        class StockDetailViewHolder(private val binding: RecyclerItemNewsBinding,
                                    private val listener: NewsRecyclerViewClickListener) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(share: SymbolPressResponse) {
                binding.apply {

                    for (i in share.majorDevelopment.indices){
                        globalArticle = share.majorDevelopment.get(i).description
                        globalHeadline = share.majorDevelopment.get(i).headline
                        globalSymbol = share.majorDevelopment.get(i).symbol

                        recyclerItemHeader.text = share.majorDevelopment.get(i).headline
                        recyclerItemSub.text = share.majorDevelopment.get(i).symbol
                        recyclerItemDate.text = share.majorDevelopment.get(i).datetime
                    }

                    root.setOnClickListener {
                        listener.onCardClicked(share)
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