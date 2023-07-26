package com.example.online_store.Customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.online_store.Item
import com.example.online_store.ItemManager
import com.example.online_store.R
import com.example.online_store.collectionRef
import com.example.online_store.convertToNumberOrZero
import com.example.online_store.customer_Item
import com.example.online_store.databinding.BuyingItemLayoutBinding
import com.example.online_store.findDocumentByName
import com.example.online_store.updateCustomer_ItemByName
import com.example.online_store.updateItemsByName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Buying_Item : Fragment() {
    private var _binding : BuyingItemLayoutBinding?  = null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    lateinit var item : Item
    var state = "cart"
    var buying_quantity ="0"



    var bought =0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BuyingItemLayoutBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("item")?.let {
            item = ItemManager.items[it]

            binding.name3.text = item.name

            binding.released3.text = item.released
            binding.rating3.text = item.rating

            binding.price3.text= item.price+" $"
            binding.quantity3.text= item.quantity+" available"
            Glide.with(requireContext()).load(item.background_image).circleCrop().into(binding.backgroundImage4)
        }




        binding.quantity8.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val strNumber = s.toString()
                try {
                    var intValue: Int = strNumber.toInt()
                    if (intValue>item.quantity.toInt()) {
                        intValue=item.quantity.toInt()
                    }
                    val price_Value2: Int = item.price.toInt()
                    val sum =(intValue*price_Value2)
                    binding.cost.text =sum.toString()+"$"


                } catch (e: NumberFormatException) {
                    // Handle the exception when the String cannot be converted to an Int
                    println("Invalid input: $strNumber is not a valid integer.")
                }


            }
            override fun afterTextChanged(s: Editable?) { }
        })


        binding.wishlist.setOnClickListener{
            state="wishlist"
            buying_quantity="0"
            binding.buy.performClick()
        }



        binding.buy.setOnClickListener{


            buying_quantity= convertToNumberOrZero(binding.quantity8.text.toString())

            if (userEmail != null) {


                var strNumber = binding.quantity8.text.toString()
                try {
                    if (state=="wishlist") {

                        strNumber="0"
                        buying_quantity="0"
                    }
                    val intValue: Int = strNumber.toInt()

                    if (intValue>item.quantity.toInt()) {
                        buying_quantity=item.quantity
                    }

                    if ((state=="cart" && buying_quantity!="0")||(state=="wishlist")) {
                        val customerItem  = customer_Item(
                            item.id,
                            item.name,
                            item.released,
                            item.rating,
                            item.background_image,
                            buying_quantity,
                            item.price,
                            state


                        )

                        var quantity_left=item.quantity.toInt()-buying_quantity.toInt()

                        val item_1  = Item(
                            item.id,
                            item.name,
                            item.released,
                            item.rating,
                            item.background_image,
                            quantity_left.toString(),
                            item.price


                        )


                        val documentName = item.name


                        val collection_userEmailPath = userEmail


                        findDocumentByName(collection_userEmailPath, documentName) { documents ->
                            if (documents.isNotEmpty()) {

                                    for (document in documents)
                                    {


                                        val quantity = document.getString("quantity")
                                        val state_item = document.getString("state")

                                        if (state_item!="history" ){
                                            if (quantity != null) {


                                                if (state == state_item)
                                                {
                                                    if (state_item=="cart")
                                                    {
                                                        var quan2 =
                                                            buying_quantity.toInt() + quantity.toInt()

                                                        val document_Item = customer_Item(
                                                            item.id,
                                                            item.name,
                                                            item.released,
                                                            item.rating,
                                                            item.background_image,
                                                            quan2.toString(),
                                                            item.price,
                                                            state


                                                        )


                                                        bought = 1
                                                        updateCustomer_ItemByName(
                                                            documentName,
                                                            document_Item,
                                                            userEmail
                                                        )
                                                    }
                                                    if ((state_item=="wishlist"))
                                                    {
                                                        bought = 1

                                                    }

                                                }
                                            }


                                        }



                                }

                            } else {


                                db.collection(userEmail).add(customerItem)

                                bought =1

                            }
                            if ( bought ==0) {
                                db.collection(userEmail).add(customerItem)
                                bought =1
                            }
                        }





                        if(quantity_left>0) {
                            updateItemsByName(customerItem.name, item_1)
                        }
                        else
                        {

                            collectionRef.get()
                                .addOnSuccessListener { querySnapshot ->
                                    val batch = db.batch()
                                    for (document in querySnapshot.documents) {
                                        if(document.getString("name")==item.name ) {
                                            batch.delete(document.reference)
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




                        findNavController().navigate(R.id.action_buying_Item_to_customer_FireBase_AllItemsFragment)

                    }
                    else
                    {
                        Toast.makeText(requireContext(),getString(R.string.there_must_be_at_least_one_game), Toast.LENGTH_SHORT).show()
                    }


                } catch (e: NumberFormatException) {
                    // Handle the exception when the String cannot be converted to an Int
                    println("Invalid input: $strNumber is not a valid integer.")
                }



            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}