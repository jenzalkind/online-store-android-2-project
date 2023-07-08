package com.example.online_store

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Api_ItemAdapter(private val itemList: ArrayList<Item>) : RecyclerView.Adapter<Api_ItemAdapter.ViewHolder>(){
    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val background_image = itemView.findViewById<ImageView>(R.id.background_image4)
        val name = itemView.findViewById<TextView>(R.id.name4)
        val rating = itemView.findViewById<TextView>(R.id.rating4)
        val released = itemView.findViewById<TextView>(R.id.released4)

        val price4 = itemView.findViewById<TextView>(R.id.price4)
        val quantity4 = itemView.findViewById<TextView>(R.id.quantity4)
        //val genres = itemView.findViewById<TextView>(R.id.category)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]


        holder.name.text = item.name
        holder.released.text = item.released
        holder.rating.text = item.rating
        holder.price4.text = item.price
        holder.quantity4.text = item.quantity


        //description

        Glide.with(context).load(item.background_image).into(holder.background_image)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}