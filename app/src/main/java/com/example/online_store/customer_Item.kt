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

                var state : String

                )
object chosenItem_customer {

    var chosenItem_customer = MutableLiveData<customer_Item>()

    fun setGame(customer_Item: customer_Item) {
        chosenItem_customer.value = customer_Item
    }

    fun getGame():customer_Item {
        return chosenItem_customer.value!!
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

