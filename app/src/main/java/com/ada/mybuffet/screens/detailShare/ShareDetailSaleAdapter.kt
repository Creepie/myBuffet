package com.ada.mybuffet.screens.detailShare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.databinding.RecyclerViewItemPurchaseBinding
import com.ada.mybuffet.screens.detailShare.model.SaleItem

class ShareDetailSaleAdapter :  ListAdapter<SaleItem, ShareDetailSaleAdapter.ShareDetailViewHolder>(
    DiffCallback()
) {

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

        fun bind(saleItem: SaleItem) {
            binding.apply {
                itemPurchaseAmount.text = saleItem.shareNumber.toString()
                itemPurchaseValue.text = String.format("€ %.2f", saleItem.getValue())
                itemPurchaseSharePrice.text = "€ ${saleItem.sharePrice}"
                itemPurchaseFee.text = "€ ${saleItem.fees}"
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<SaleItem>() {
        override fun areItemsTheSame(oldItem: SaleItem, newItem: SaleItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SaleItem, newItem: SaleItem): Boolean =
            oldItem == newItem
    }
}