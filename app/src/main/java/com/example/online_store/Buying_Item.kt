package com.example.online_store

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.online_store.databinding.BuyingItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Buying_Item : Fragment() {

    private var _binding : BuyingItemLayoutBinding?  = null

    private val binding get() = _binding!!


    val db = FirebaseFirestore.getInstance()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    lateinit var item :Item

    var state = "cart"

    var buying_quantity ="0"


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
        //val item = chosenItem.getGame()///ItemManager.items[it]

            item = ItemManager.items[it]

            binding.name3.text = item.name

            binding.released3.text = item.released
            binding.rating3.text = item.rating

            binding.price3.text= item.price+" $"
            binding.quantity3.text= item.quantity+" available"

            Glide.with(requireContext()).load(item.background_image).circleCrop()
                .into(binding.backgroundImage4)

        }




        binding.quantity8.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
                // You can perform any pre-processing here if needed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
                // Update the TextView with the new text from EditText


                val strNumber = s.toString()
                try {
                    val intValue: Int = strNumber.toInt()
                    val intValue2: Int = item.price.toInt()
                    val sum =(intValue*intValue2)
                    binding.cost.text =sum.toString()+"$"


                } catch (e: NumberFormatException) {
                    // Handle the exception when the String cannot be converted to an Int
                    println("Invalid input: $strNumber is not a valid integer.")
                }


            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                // You can perform any post-processing here if needed.
            }
        })


        binding.wishlist.setOnClickListener{
            state="wishlist"
            buying_quantity="0"
            binding.buy.performClick()
        }



        binding.buy.setOnClickListener{

            buying_quantity=binding.quantity8.text.toString()

            if (userEmail != null) {


                var strNumber = binding.quantity8.text.toString()
                try {
                    if (state=="wishlist") {

                        strNumber="0"
                        buying_quantity="0"
                    }
                    val intValue: Int = strNumber.toInt()

                    if (intValue>item.quantity.toInt())
                    {

                        buying_quantity=item.quantity
                    }



                    if ((state=="cart" && buying_quantity!="0")||(state=="wishlist"))
                    {
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

                        val item  = Item(
                            item.id,
                            item.name,
                            item.released,
                            item.rating,
                            item.background_image,
                            quantity_left.toString(),
                            item.price


                        )

                        db.collection(userEmail).add(customerItem)


                        if(quantity_left>0) {
                            updateItemsByName(customerItem.name, item)
                        }
                        else
                        {

                            collectionRef.get()
                                .addOnSuccessListener { querySnapshot ->
                                    val batch = db.batch()
                                    for (document in querySnapshot.documents) {
                                        if(document.getString("id")==item.id ) {
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