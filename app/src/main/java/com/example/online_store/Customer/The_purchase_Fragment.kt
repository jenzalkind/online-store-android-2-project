package com.example.online_store.Customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.online_store.R
import com.example.online_store.customer_Item
import com.example.online_store.databinding.ThePurchaseBinding
import com.example.online_store.db
import com.example.online_store.total.total_
import com.google.firebase.auth.FirebaseAuth

class the_purchase_Fragment : Fragment() {

    private var _binding : ThePurchaseBinding?  = null

    private val binding get() = _binding!!



    var mony=0

    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ThePurchaseBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (userEmail != null) {

            mony = 0
            val collectionRef = db.collection(userEmail)
            var sum = 0
            collectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = db.batch()
                    for (document in querySnapshot.documents) {



                        if (document.getString("state") == "cart") {

                            try {
                                var price = document.getString("price")
                                var quantity = document.getString("quantity")
                                if (price?.toInt() != null) {
                                    if (quantity != null) {
                                        sum = price.toInt() * quantity.toInt()
                                        mony += sum

                                        total_.value= mony.toString()

                                        //showSimpleDialog()
                                        binding.sum.text = total_.value.toString()


                                    }

                                }
                            } catch (_: ArithmeticException) {

                            }
                        }


                    }


                }
                .addOnFailureListener { exception ->
                    // Error occurred while fetching documents from the collection
                }

        }





        binding.agree.setOnClickListener {

            if (userEmail != null) {

                val collectionRef = db.collection(userEmail)

                var sum = 0
                collectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {


                            if (document.getString("state") == "cart") {

                                try {
                                    val customer_item_history= customer_Item(

                                        document.getString("id").toString(),
                                        document.getString("name").toString(),
                                        document.getString("released").toString(),
                                        document.getString("rating").toString(),
                                        document.getString("background_image").toString(),
                                        document.getString("quantity").toString(),
                                        document.getString("price").toString(),
                                        "history"


                                    )


                                    val itemId = document.id
                                    customer_item_history.id = itemId // Ensure the ID is set in the updatedItem object

                                    // Update the item document with the new values
                                    collectionRef.document(itemId).set(customer_item_history)



                                } catch (_: ArithmeticException) {

                                }
                            }


                        }

                        batch.commit()
                            .addOnSuccessListener {
                                // Collection cleared successfully
                            }
                            .addOnFailureListener { exception ->
                                // Error occurred while clearing the collection
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Error occurred while fetching documents from the collection
                    }


            }


            findNavController().navigate(
                R.id.action_the_purchase_Fragment_to_customer_FireBase_AllItemsFragment
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}