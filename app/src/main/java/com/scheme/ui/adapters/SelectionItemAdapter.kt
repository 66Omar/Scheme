package com.scheme.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.scheme.R
import com.scheme.ui.adapters.SelectionItemAdapter.ItemViewHolder

class SelectionItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    private var items: List<String>? = null
    val itemClicked = MutableLiveData<String>()

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item: TextView = itemView.findViewById(R.id.item)
        var itemCard: LinearLayout = itemView.findViewById(R.id.itemCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.selection_item, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current_item = items!![position]
        holder.item.text = current_item
        holder.itemCard.setOnClickListener {
            itemClicked.value = current_item
        }
    }

    override fun getItemCount(): Int {
        return if (items != null) {
            items!!.size
        } else 0
    }

    fun setList(newList: List<String>?) {
        items = newList
        notifyDataSetChanged()
    }
}