package com.ada.mybuffet.screens.detailShare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemFeeBinding
import com.ada.mybuffet.screens.detailShare.model.FeeItem

/**
 * @author Paul Pfisterer
 * Adapter for the fees recycler view
 */
class ShareDetailFeeAdapter :  ListAdapter<FeeItem, ShareDetailFeeAdapter.ShareDetailViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareDetailViewHolder {
        val binding = RecyclerViewItemFeeBinding.inflate(
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

    class ShareDetailViewHolder(private val binding: RecyclerViewItemFeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //Values are set here
        fun bind(feeItem: FeeItem) {
            binding.apply {
                itemFeeAmount.text = String.format("€ %.2f", feeItem.amount.toDouble())
                itemFeeDescription.text = feeItem.description
                itemFeeDate.text = feeItem.getFormattedDate()
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<FeeItem>() {
        override fun areItemsTheSame(oldItem: FeeItem, newItem: FeeItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FeeItem, newItem: FeeItem): Boolean =
            oldItem == newItem
    }
}