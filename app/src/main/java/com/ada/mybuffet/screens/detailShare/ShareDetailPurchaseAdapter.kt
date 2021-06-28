package com.ada.mybuffet.screens.detailShare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemPurchaseBinding
import com.ada.mybuffet.screens.detailShare.model.Purchase

class ShareDetailPurchaseAdapter :
    ListAdapter<Purchase, ShareDetailPurchaseAdapter.ShareDetailViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareDetailViewHolder {
        val binding = RecyclerViewItemPurchaseBinding.inflate(
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

    class ShareDetailViewHolder(private val binding: RecyclerViewItemPurchaseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(purchase: Purchase) {
            val value = purchase.sharePrice.toDouble() * purchase.shareNumber

            binding.apply {
                itemPurchaseAmount.text = purchase.shareNumber.toString()
                itemPurchaseValue.text = String.format("€ %.2f", value)
                itemPurchaseSharePrice.text = "€ ${purchase.sharePrice}"
                itemPurchaseFee.text = "€ ${purchase.fees}"
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Purchase>() {
        override fun areItemsTheSame(oldItem: Purchase, newItem: Purchase): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Purchase, newItem: Purchase): Boolean =
            oldItem == newItem
    }
}