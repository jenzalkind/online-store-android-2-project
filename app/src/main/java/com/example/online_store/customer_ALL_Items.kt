package com.example.online_store

import androidx.lifecycle.MutableLiveData
import java.util.UUID

data class customer_ALL_Items(

    val userEmail: String,

    var customer_Items : List<customer_Item>,

                )
/*
object chosen_ALL_Item_customer {

    var chosenItem = MutableLiveData<Item>()

    fun setGame(item: Item) {
        chosenItem.value = item
    }

    fun getGame():Item {
        return chosenItem.value!!
    }
}*/
/*object ALL_ItemManager_customer {

    val customer_items : MutableList<customer_Item> = mutableListOf()

    fun add(customer_item: customer_Item) {
        customer_items.add(customer_item)
    }

    fun remove(index:Int) {
        customer_items.removeAt(index)
    }
}*/


