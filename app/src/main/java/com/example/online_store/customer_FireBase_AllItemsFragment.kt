package com.example.online_store

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.example.online_store.databinding.CustomerFireBaseAllItemsLayoutBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/////// FireBase


class customer_FireBase_AllItemsFragment : Fragment() {

    private var _binding : CustomerFireBaseAllItemsLayoutBinding? = null

    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()
//    val collectionRef = db.collection("Item")


    var sold_out=0


    var shop_quantity="0"
    //var ALL_Item_customer= searchItemsByName(null,"cart")

    //var ALL_ItemManager_customer = ALL_Item_customer.toMutableList()

    //val db2 = Firebase.firestore
    val userEmail = FirebaseAuth.getInstance().currentUser?.email


    var ALL_ItemManager_customer : MutableList<customer_Item> = mutableListOf()

    lateinit var search_list: List<customer_Item>


    var mony=0

    var state = "cart"

    //TODO XXXX




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomerFireBaseAllItemsLayoutBinding.inflate(inflater,container,false)

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.fab.setOnClickListener {

            //ItemManager.items.clear()
            findNavController().navigate(R.id.action_customer_FireBase_AllItemsFragment_to_customer_buy_ItemsFragment)
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////


        binding.fab3.setOnClickListener {
            if (userEmail != null) {

                val collectionRef = db.collection(userEmail)

                var sum= 0
                collectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {


                            if (document.getString("state") == "cart") {

                                try {
                                    var price= document.getString("price")
                                    var quantity =document.getString("quantity")
                                    if(price?.toInt()!=null) {
                                        if (quantity != null) {
                                            sum = price.toInt() * quantity.toInt()
                                            mony += sum
                                        }

                                    }
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



            showSimpleDialog()
        }





        runBlocking {
            if (userEmail != null) {


                val collectionName = userEmail
                val partialName = ""
                val minPrice = null
                val maxPrice = null


                val search_list =
                    searchItems_customer(collectionName, partialName, minPrice, maxPrice, state)


                ALL_ItemManager_customer.clear()

                //val searchResults: List<customer_Item> = searchItemsByName(null, "cart")
                ALL_ItemManager_customer = search_list.toMutableList()
            }
        }








        binding.searchButton.setOnClickListener{
            runBlocking {

                if (userEmail != null) {


                    val collectionName = userEmail
                    val partialName = binding.search.text.toString()
                    val minPrice = null
                    val maxPrice = null

                    search_list =
                        searchItems_customer(collectionName, partialName, minPrice, maxPrice, state)


                    ALL_ItemManager_customer.clear()
                    ALL_ItemManager_customer = search_list.toMutableList()


                    //search_list=searchItemsByName(binding.search.text.toString(),"cart")
                    binding.recycler.adapter =
                        Adapter_customer_FireBase_Item(
                            ALL_ItemManager_customer,
                            object : Adapter_customer_FireBase_Item.ItemListener {

                                override fun onItemClicked(index: Int) {

                                    //TODO edit to already buy for all onItemClicked


                                    ALL_ItemManager_customer[index]


                                    chosenItem_customer.setGame(ALL_ItemManager_customer[index])

                                    if (state=="cart")
                                    {
                                        findNavController().navigate(
                                            R.id.action_customer_FireBase_AllItemsFragment_to_buying_edit_Item,
                                            bundleOf("item" to index))

                                    }
                                    if (state=="wishlist")
                                    {

/*                                        findNavController().navigate(
                                            R.id.action_customer_FireBase_AllItemsFragment_to_buying_Item,
                                            bundleOf("item" to index))*/

                                    }


                                    ///////////////////////////////////////////////////////////////////////////////////////////////


                                }

                                override fun onItemLongClicked(index: Int) {
                                    /*findNavController().navigate(
                                    R.id.action_fireBase_AllItemsFragment_to_edit_ItemFragment,
                                    bundleOf("item" to index)
                                )*/
                                    ///////////////////////////////////////////////////////////////////////////////////////////////

                                }
                            })

                }


            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }



        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_history -> {


                    state= "history"

                    binding.searchButton.performClick()

                    //findNavController().navigate(R.id.)

                    /////////////////////////////////////////////////////////////////////////////////
                    true
                }


                R.id.nav_cart -> {


                    state= "cart"
                    binding.searchButton.performClick()


                    /////////////////////////////////////////////////////////////////////////////////
                    true
                }



                R.id.nav_wishlist -> {


                    state= "wishlist"
                    binding.searchButton.performClick()


                    /////////////////////////////////////////////////////////////////////////////////
                    true
                }
                else -> false
            }
        }





        binding.recycler.adapter =

            Adapter_customer_FireBase_Item(ALL_ItemManager_customer, object : Adapter_customer_FireBase_Item.ItemListener {

                override fun onItemClicked(index: Int) {

                    Toast.makeText(
                        requireContext(),
                        "${ALL_ItemManager_customer[index]}", Toast.LENGTH_SHORT
                    ).show()
                    chosenItem_customer.setGame(ALL_ItemManager_customer[index])


                    findNavController().navigate(
                        R.id.action_customer_FireBase_AllItemsFragment_to_buying_edit_Item,
                        bundleOf("item" to index)
                    )
                    ///////////////////////////////////////////////////////////////////////////////////////////////

                }

                override fun onItemLongClicked(index: Int) {
                    /*                            findNavController().navigate(
                                                    R.id.action_fireBase_AllItemsFragment_to_edit_ItemFragment,
                                                    bundleOf("item" to index)
                                                )*/
                    ///////////////////////////////////////////////////////////////////////////////////////////////

                }
            })



        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                var deletedItem = ALL_ItemManager_customer[position].id
                var deletedItem_item =ALL_ItemManager_customer[position]

                ALL_ItemManager_customer.removeAt(viewHolder.adapterPosition)
                binding.recycler.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)

                if (userEmail != null) {


                    val collectionRef = db.collection(userEmail)

                    collectionRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            val batch = db.batch()
                            for (document in querySnapshot.documents) {
                                var dd =document.id


                                if (document.id == deletedItem) {
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


                    if (state=="cart")
                    {
                        val documentName = deletedItem_item.name


                        val collectionPath = "Item"


                        findDocumentByName(collectionPath, documentName) { documents ->
                            if (documents.isNotEmpty()) {
                                // Document found, do something with it
                                val document = documents[0] // Assuming there's only one document with the given name

                                val quantity = document.getString("quantity")
                                if (quantity != null) {
                                    // Use the "name" value here
                                    // For example, you can display it in a TextView



                                    var quan =deletedItem_item.quantity.toInt()+quantity.toInt()

                                    val documentName2  = Item(
                                        deletedItem_item.id,
                                        deletedItem_item.name,
                                        deletedItem_item.released,
                                        deletedItem_item.rating,
                                        deletedItem_item.background_image,
                                        quan.toString(),
                                        deletedItem_item.price


                                    )


                                    updateItemsByName(documentName,documentName2)






                                }
                                // Access document fields using document["field_name"]


                            } else {



                                val item2  = Item(
                                    deletedItem_item.id,
                                    deletedItem_item.name,
                                    deletedItem_item.released,
                                    deletedItem_item.rating,
                                    deletedItem_item.background_image,
                                    deletedItem_item.quantity,
                                    deletedItem_item.price


                                )
                                db.collection("Item").add(item2)


                                // Document not found
                            }
                        }










                    }



                }



            }





        }).attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Purchase")
        builder.setMessage("Please note that you are about to make a purchase for ${mony}. All sales are final.")

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            //findNavController().navigate(R.id.action_customer_FireBase_AllItemsFragment_to_customer_buy_ItemsFragment,bundleOf("mony" to mony))
        }

        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            // Do something when the user clicks the "Cancel" button (optional)
        }

        val dialog = builder.create()
        dialog.show()
    }


}
