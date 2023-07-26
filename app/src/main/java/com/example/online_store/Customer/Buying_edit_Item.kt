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
import com.example.online_store.R
import com.example.online_store.chosenItem_customer
import com.example.online_store.convertToNumberOrZero
import com.example.online_store.customer_Item
import com.example.online_store.databinding.BuyingEditItemLayoutBinding
import com.example.online_store.findDocumentByName
import com.example.online_store.updateCustomer_ItemByName
import com.example.online_store.updateItemsByName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Buying_edit_Item : Fragment() {

    private var _binding : BuyingEditItemLayoutBinding?  = null

    private val binding get() = _binding!!


    val db = FirebaseFirestore.getInstance()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    lateinit var item : customer_Item

    var state = "cart"

    var buying_quantity ="0"
    var shop_quantity ="0"

    val collectionRef = db.collection("Item")


    var sold_out =0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BuyingEditItemLayoutBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("item")?.let {
        //val item = chosenItem.getGame()///ItemManager.items[it]

            val ss = chosenItem_customer.chosenItem_customer.value?.name
            item = chosenItem_customer.getGame()


            binding.name3.text = item.name

            binding.released3.text = item.released
            binding.rating3.text = item.rating

            binding.price3.text= item.price+" $"


            val collectionPath = "Item"
            val documentName = item.name


            findDocumentByName(collectionPath, documentName) { documents ->
                if (documents.isNotEmpty()) {
                    // Document found, do something with it
                    val document = documents[0]

                    val quantity = document.getString("quantity")
                    if (quantity != null) {
                        // Use the "name" value here
                        // For example, you can display it in a TextView
                        binding.quantity3.text=quantity
                        shop_quantity=quantity
                    }



                } else {

                    sold_out=1
                    binding.quantity3.text="0"

                    shop_quantity="0"
                    // Document not found
                }
            }


            binding.quantity8.setText(item.quantity)

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
                    binding.cost.text =sum.toString()+" $"


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


            buying_quantity= convertToNumberOrZero(binding.quantity8.text.toString())

            if (userEmail != null) {


                var strNumber = binding.quantity8.text.toString()
                try {
                    val intValue: Int = strNumber.toInt()

                    if (state=="wishlist") {

                        strNumber = "0"
                        buying_quantity = "0"




                    }

                    if (intValue>=item.quantity.toInt()+shop_quantity.toInt())
                    {
                        val all_item_transaction=item.quantity.toInt()+shop_quantity.toInt()
                        buying_quantity=all_item_transaction.toString()
                    }



                    if ((state=="cart" && buying_quantity!="0")||(state=="wishlist"))
                    {

                        if(sold_out==0)
                        {
                            var quantity_left =shop_quantity.toInt()+item.quantity.toInt()-buying_quantity.toInt()


                            val item  = Item(
                                item.id,
                                item.name,
                                item.released,
                                item.rating,
                                item.background_image,
                                quantity_left.toString(),
                                item.price


                            )
                            updateItemsByName(item.name, item)

                        }
                        else
                        {
                            if(sold_out==1)
                            {
                                var  rr= item.quantity.toInt()

                                var  rr2= shop_quantity.toInt()

                                var  r3= buying_quantity.toInt()


                                if(buying_quantity.toInt()<=item.quantity.toInt()) {
                                    val quantity_return=item.quantity.toInt()-buying_quantity.toInt()
                                    val item2 = Item(
                                        item.id,
                                        item.name,
                                        item.released,
                                        item.rating,
                                        item.background_image,
                                        quantity_return.toString(),
                                        item.price
                                    )

                                    if (quantity_return.toInt()!=0) {
                                        db.collection("Item").add(item2)
                                    }
                                }
                            }
                        }











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

                        updateCustomer_ItemByName(item.name,customerItem,userEmail)



                        var quantity_left=buying_quantity.toInt()-item.quantity.toInt()+shop_quantity.toInt()

                        if(quantity_left<=0&&sold_out!=1)
                        {

                            collectionRef.get()
                                .addOnSuccessListener { querySnapshot ->
                                    val batch = db.batch()
                                    for (document in querySnapshot.documents) {


                                        if(document.getString("name")==item.name)
                                        {
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




                        findNavController().navigate(R.id.action_buying_edit_Item_to_customer_FireBase_AllItemsFragment)

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