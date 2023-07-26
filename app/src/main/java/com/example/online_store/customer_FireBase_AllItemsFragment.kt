package com.example.online_store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.online_store.databinding.CustomerFireBaseAllItemsLayoutBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation

class customer_FireBase_AllItemsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: CustomerFireBaseAllItemsLayoutBinding? = null
    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var ALL_ItemManager_customer: MutableList<customer_Item> = mutableListOf()
    lateinit var search_list: List<customer_Item>




    var state = "cart"

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = "Game Hub"
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomerFireBaseAllItemsLayoutBinding.inflate(inflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.fab.setOnClickListener {

            findNavController().navigate(R.id.action_customer_FireBase_AllItemsFragment_to_customer_buy_ItemsFragment)
        }


        binding.fab3.setOnClickListener {

                findNavController().navigate(
                    R.id.action_customer_FireBase_AllItemsFragment_to_the_purchase_Fragment
                )

        }


        runBlocking {
            if (userEmail != null) {
                val collectionName = userEmail
                val partialName = ""
                val minPrice = null
                val maxPrice = null
                val search_list = searchItems_customer(collectionName, partialName, minPrice, maxPrice, state)
                ALL_ItemManager_customer.clear()
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


                    binding.recycler.adapter =
                        Adapter_customer_FireBase_Item(
                            ALL_ItemManager_customer,
                            object : Adapter_customer_FireBase_Item.ItemListener {

                                override fun onItemClicked(index: Int) {

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


                                    }


                                }

                                override fun onItemLongClicked(index: Int) {
                                    //TODO item detail maybe can connect to detail

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

                }


                R.id.nav_cart -> {


                    state= "cart"
                    binding.searchButton.performClick()


                    true
                }



                R.id.nav_wishlist -> {


                    state= "wishlist"
                    binding.searchButton.performClick()



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
                }

                override fun onItemLongClicked(index: Int) {
                    //TODO item detail maybe can connect to detail

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
                var deletedItem = ALL_ItemManager_customer[position]
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




                                if(document.getString("name")==deletedItem.name )
                                {
                                    batch.delete(document.reference)
                                }
                            }
                            batch.commit()
                                .addOnSuccessListener {
                                    Log.d(tag, "Collection cleared successfully")
                                }
                                .addOnFailureListener {
                                    Log.d(tag, "Error occurred while clearing the collection")
                                }
                        }
                        .addOnFailureListener {
                            Log.d(tag, "Error occurred while fetching documents from the collection")
                        }


                    if (state=="cart")
                    {
                        val documentName = deletedItem_item.name


                        val collectionPath = "Item"


                        findDocumentByName(collectionPath, documentName) { documents ->
                            if (documents.isNotEmpty()) {
                                val document = documents[0]
                                val quantity = document.getString("quantity")
                                if (quantity != null) {



                                    var quan =deletedItem_item.quantity.toInt()+quantity.toInt()

                                    val document_Item  = Item(
                                        deletedItem_item.id,
                                        deletedItem_item.name,
                                        deletedItem_item.released,
                                        deletedItem_item.rating,
                                        deletedItem_item.background_image,
                                        quan.toString(),
                                        deletedItem_item.price


                                    )


                                    updateItemsByName(documentName,document_Item)



                                }


                            } else {
                                val item2 = Item(
                                    deletedItem_item.id,
                                    deletedItem_item.name,
                                    deletedItem_item.released,
                                    deletedItem_item.rating,
                                    deletedItem_item.background_image,
                                    deletedItem_item.quantity,
                                    deletedItem_item.price
                                )
                                db.collection("Item").add(item2)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutBtn) {
            firebaseAuth.signOut()
            Navigation.findNavController(binding.root).navigate(R.id.action_customer_FireBase_AllItemsFragment_to_loginFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}
