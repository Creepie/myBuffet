package com.ada.mybuffet.features

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ada.mybuffet.R
import com.ada.mybuffet.repo.SymbolPressResponse


class NewsRecyclerAdapter(private val list: MutableList<SymbolPressResponse>): RecyclerView.Adapter<NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(
           R.layout.recycler_item_news,
           parent,
           false
       )
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val item = list[position]

        holder.header.text = item.majorDevelopment[0].headline
        holder.sub.text = item.symbol
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class NewsViewHolder(view: View): RecyclerView.ViewHolder(view){
    val header = view.findViewById<TextView>(R.id.recycler_item_header)
    val sub = view.findViewById<TextView>(R.id.recycler_item_sub)
}