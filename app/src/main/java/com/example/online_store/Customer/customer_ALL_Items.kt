package com.example.online_store

import androidx.lifecycle.MutableLiveData
import java.util.UUID

data class customer_ALL_Items(

    val userEmail: String,

    var customer_Items : List<customer_Item>,

                )
object total {

    var total_ = MutableLiveData<String>()

    fun setGame(total: String) {
        total_.value = total
    }

    fun getGame():String {
        return total_.value!!
    }
}


