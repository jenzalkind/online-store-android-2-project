package com.example.online_store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.online_store.databinding.ItemsBinding

class Adapter_customer_FireBase_Item(val customer_Items___:List<customer_Item>, val callBack:ItemListener)
    : RecyclerView.Adapter<Adapter_customer_FireBase_Item.ItemViewHolder>() {

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

        fun bind(customer_Item:customer_Item) {

            binding.name4.text = customer_Item.name
            binding.released4.text = customer_Item.released
            binding.rating4.text = customer_Item.rating
            Glide.with(binding.root).load(customer_Item.background_image).circleCrop().into(binding.backgroundImage4)
            binding.price4.text =customer_Item.price+" $"
            binding.quantity4.text =customer_Item.quantity + " quantity"


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(ItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(customer_Items___[position])

    override fun getItemCount() =
        customer_Items___.size

}