package com.ada.mybuffet.screens.detailShare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemDividendBinding
import com.ada.mybuffet.screens.detailShare.model.DividendItem

class ShareDetailDividendAdapter :  ListAdapter<DividendItem, ShareDetailDividendAdapter.ShareDetailViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareDetailViewHolder {
        val binding = RecyclerViewItemDividendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShareDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShareDetailViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ShareDetailViewHolder(private val binding: RecyclerViewItemDividendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dividendItem: DividendItem) {
            binding.apply {
                itemDividendAmount.text = String.format("€ %.2f", dividendItem.amount.toDouble())
                itemDividendNumber.text = dividendItem.number.toString()
                itemDividendDate.text = dividendItem.getFormattedDate()
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<DividendItem>() {
        override fun areItemsTheSame(oldItem: DividendItem, newItem: DividendItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DividendItem, newItem: DividendItem): Boolean =
            oldItem == newItem
    }
}