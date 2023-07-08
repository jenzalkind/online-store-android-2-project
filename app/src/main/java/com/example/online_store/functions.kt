package com.example.online_store

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Random

val db = FirebaseFirestore.getInstance()




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

suspend fun searchItemsByName(partialName: String): List<Item> {

    val collectionName = "Item"
    val collectionRef = db.collection(collectionName)

    val querySnapshot = collectionRef
        .orderBy("name")
        .startAt(partialName.lowercase())
        .endAt(partialName.lowercase() + "\uf8ff")
        .get()
        .await()

    val items = mutableListOf<Item>()
    for (document in querySnapshot.documents) {
        val itemId = document.id
        val itemData = document.data

        val item = Item(
            itemId,
            itemData?.get("name") as String,
            itemData.get("released") as String,
            itemData.get("rating") as String,
            itemData.get("background_image") as String,
            itemData.get("quantity") as String,
            itemData.get("price") as String,
        )
        items.add(item)
    }

    return items
}

































