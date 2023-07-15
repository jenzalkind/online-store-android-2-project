package com.example.online_store

import androidx.lifecycle.MutableLiveData
import java.util.UUID

data class customer_Item(var id:String,
                var name: String,
                var released: String,
                var rating: String,
                /*,var genres :String,,*/
                var background_image: String,
                var quantity : String ,
                var price :String,

                var need_to_buy : String,
                var cart :String

                )
object chosenItem_customer {

    var chosenItem = MutableLiveData<Item>()

    fun setGame(item: Item) {
        chosenItem.value = item
    }

    fun getGame():Item {
        return chosenItem.value!!
    }
}
object ItemManager_customer {

    val items : MutableList<Item> = mutableListOf()

    fun add(item: Item) {
        items.add(item)
    }

    fun remove(index:Int) {
        items.removeAt(index)
    }
}

