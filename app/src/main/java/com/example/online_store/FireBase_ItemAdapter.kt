package com.example.online_store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.online_store.databinding.ItemsBinding

class FireBase_ItemAdapter(val items:List<Item>, val callBack:ItemListener)
    : RecyclerView.Adapter<FireBase_ItemAdapter.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index:Int)
        fun onItemLongClicked(index:Int)
    }

    inner class ItemViewHolder(private val binding: ItemsBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener,
        View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            callBack.onItemClicked(adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            callBack.onItemLongClicked(adapterPosition)
            return false
        }

        fun bind(item:Item) {

            binding.name4.text = item.name
            binding.released4.text = item.released
            binding.rating4.text = item.rating
            Glide.with(binding.root).load(item.background_image).circleCrop().into(binding.backgroundImage4)
            binding.price4.text =item.price
            binding.quantity4.text =item.quantity


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(ItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() =
        items.size

}