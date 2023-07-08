package com.example.online_store

import java.util.UUID

data class Item(var id:String,
                var name: String,
                var released: String,
                var rating: String,
                /*,var genres :String,,*/
                var background_image: String,
                var quantity : String ,
                var price :String

                )


object ItemManager {

    val items : MutableList<Item> = mutableListOf()

    fun add(item: Item) {
        items.add(item)
    }

    fun remove(index:Int) {
        items.removeAt(index)
    }
}