package com.example.online_store

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Random

val db = FirebaseFirestore.getInstance()
val collectionRef = db.collection("Item")




fun RandomQuantity(): Int {
    val min = 50
    val max = 80

    val random = Random()
    return random.nextInt(max - min + 1) + min
}


fun RandomPrice(): Int {
    val min = 20
    val max = 120

    val random = Random()
    return random.nextInt(max - min + 1) + min
}


suspend fun deleteAndAddItem(
    romin_id: String,
    name: String,
    released: String,
    rating: String,
    backgroundImage: String,
    quantity : String,
    price :String

        ) {
    try {
        val collectionRef = db.collection("Item")

        val querySnapshot = collectionRef.get().await()
        val batch = db.batch()

        for (document in querySnapshot.documents) {
            if (document.getString("id") == romin_id) {
                batch.delete(document.reference)
            }
        }

        batch.commit().await()



        val item = Item(
            id = romin_id,
            name = name,
            released = released,
            rating = rating,
            background_image = backgroundImage,
            quantity = quantity ,
            price = price
        )

        ItemManager.add(item)

        db.collection("Item").add(item)


    } catch (e: Exception) {
        // Handle the exception
        e.printStackTrace()
    }
}



suspend fun  searchItems(
    partialName: String,
    minPrice: String?,
    maxPrice: String?
): List<Item> {
    val collectionName = "Item"
    val collectionRef = db.collection(collectionName)

    var query = collectionRef.orderBy("name")
        .startAt(partialName.lowercase())
        .endAt(partialName.lowercase() + "\uf8ff")

    val querySnapshot = query.get().await()

    val items = mutableListOf<Item>()
    for (document in querySnapshot.documents) {
        val itemId = document.id
        val itemData = document.data

        val itemPrice = itemData?.get("price") as? String
        if (itemPrice != null && isWithinPriceRange(itemPrice, minPrice, maxPrice)) {
            val item = Item(
                itemId,
                itemData["name"] as String,
                itemData["released"] as String,
                itemData["rating"] as String,
                itemData["background_image"] as String,
                itemData["quantity"] as String,
                itemData["price"] as String,

            )
            items.add(item)
        }
    }

    return items
}

private fun isWithinPriceRange(price: String, minPrice: String?, maxPrice: String?): Boolean {
    if (minPrice != null && minPrice!=""&& price!="") {
        if ( price.toInt() < minPrice.toInt())
        {
            return false
        }
    }
    if (maxPrice != null && maxPrice!="" && price!="" ) {
        if ( price.toInt() > maxPrice.toInt()) {
            return false
        }
    }
    return true
}




suspend fun searchItems_customer(
    collectionName: String,
    partialName: String,
    minPrice: String?,
    maxPrice: String?,
    state: String?
): List<customer_Item> {
    val collectionRef = db.collection(collectionName)

    var query = collectionRef.orderBy("name")
        .startAt(partialName.lowercase())
        .endAt(partialName.lowercase() + "\uf8ff")



    val querySnapshot = query.get().await()

    val items = mutableListOf<customer_Item>()
    for (document in querySnapshot.documents) {
        val itemId = document.id
        val itemData = document.data

        val state1 = itemData?.get("state") as? String

        val itemPrice = itemData?.get("price") as? String
        if (
            itemPrice != null
            &&
            isWithinPriceRange(itemPrice, minPrice, maxPrice)
            &&
            state1==state
                ) {
            val customer_Item = customer_Item(
                itemId,
                itemData["name"] as String,
                itemData["released"] as String,
                itemData["rating"] as String,
                itemData["background_image"] as String,
                itemData["quantity"] as String,
                itemData["price"] as String,
                itemData["state"] as String
            )
            items.add(customer_Item)
        }
    }

    return items
}












//  update items by name
fun updateItemsByName(itemName: String, updatedItem: Item) {
    collectionRef.whereEqualTo("name", itemName).get()
        .addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null) {
                    for (document in documents) {
                        // Get the document ID and update the item
                        val itemId = document.id
                        updatedItem.id = itemId // Ensure the ID is set in the updatedItem object

                        // Update the item document with the new values
                        collectionRef.document(itemId).set(updatedItem)
                            .addOnSuccessListener {
                                // Update success for this item
                                // You can handle success here, like showing a toast or performing additional actions.
                            }
                            .addOnFailureListener { e ->
                                // Update failed for this item
                                // Handle the error, show a toast, or log the error for debugging.
                            }
                    }
                }
            } else {
                // Handle query error
                // Show a toast or log the error for debugging.
            }
        }
}

fun updateCustomer_ItemByName(itemName: String, updatedItem: customer_Item,userEmail: String,) {

    val collectionRef2 = db.collection(userEmail)


    collectionRef2.whereEqualTo("name", itemName).get()
        .addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                val documents = task.result?.documents
                if (documents != null) {
                    for (document in documents) {
                        // Get the document ID and update the item
                        val itemId = document.id
                        updatedItem.id = itemId // Ensure the ID is set in the updatedItem object

                        // Update the item document with the new values
                        collectionRef2.document(itemId).set(updatedItem)
                            .addOnSuccessListener {
                                // Update success for this item
                                // You can handle success here, like showing a toast or performing additional actions.
                            }
                            .addOnFailureListener { e ->
                                // Update failed for this item
                                // Handle the error, show a toast, or log the error for debugging.
                            }
                    }
                }
            } else {
                // Handle query error
                // Show a toast or log the error for debugging.
            }
        }
}





fun findDocumentByName(collectionPath: String, name: String, onComplete: (List<DocumentSnapshot>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection(collectionPath)
        .whereEqualTo("name", name) // Change "name" to the field name that holds the document name
        .get()
        .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
            querySnapshot?.let {
                val documents = it.documents
                onComplete(documents)
            }
        }
        .addOnFailureListener { exception ->
            // Handle failure here if needed
            onComplete(emptyList())
        }
}




