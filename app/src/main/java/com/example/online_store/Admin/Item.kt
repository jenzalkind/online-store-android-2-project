package com.example.online_store

import androidx.lifecycle.MutableLiveData
import java.util.UUID

data class Item(var id:String,
                var name: String,
                var released: String,
                var rating: String,
                var background_image: String,
                var quantity : String ,
                var price :String

                )
object chosenItem {

    var chosenItem = MutableLiveData<Item>()

    fun setGame(item: Item) {
        chosenItem.value = item
    }

    fun getGame():Item {
        return chosenItem.value!!
    }
}
object ItemManager {

    val items : MutableList<Item> = mutableListOf()

    fun add(item: Item) {
        items.add(item)
    }

    fun remove(index:Int) {
        items.removeAt(index)
    }
}

