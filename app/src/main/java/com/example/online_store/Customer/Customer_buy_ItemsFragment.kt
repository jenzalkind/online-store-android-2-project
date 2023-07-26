package com.example.online_store.Customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController

import com.example.online_store.databinding.CustomerBuyItemsLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.online_store.Admin.FireBase_ItemAdapter
import com.example.online_store.Item
import com.example.online_store.ItemManager
import com.example.online_store.R
import com.example.online_store.convertToNumberOrZero
import com.example.online_store.searchItems
import com.google.firebase.auth.FirebaseAuth

class customer_buy_ItemsFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding : CustomerBuyItemsLayoutBinding? = null

    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Item")

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomerBuyItemsLayoutBinding.inflate(inflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        var search_list: List<Item>

        binding.searchButton.setOnClickListener{
            runBlocking {

                val min_search = convertToNumberOrZero(binding.min.text.toString())
                val max_search = convertToNumberOrZero(binding.max.text.toString())

                binding.min.setText(min_search)
                binding.max.setText(max_search)


                search_list= searchItems(binding.search.text.toString(),min_search,max_search)

                ItemManager.items.clear()
                for (item in search_list) {
                    ItemManager.add(item)

                }

                binding.recycler.adapter =
                    FireBase_ItemAdapter(
                        ItemManager.items,
                        object : FireBase_ItemAdapter.ItemListener {

                        override fun onItemClicked(index: Int) {

                            Toast.makeText(
                                requireContext(),
                                "${ItemManager.items[index]}", Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(
                                R.id.action_customer_buy_ItemsFragment_to_buying_Item,
                                bundleOf("item" to index)
                            )


                        }

                        override fun onItemLongClicked(index: Int) {
                            //TODO item detail maybe can connect to detail

                        }
                    })




            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }

        runBlocking {
            ItemManager.items.clear()
            collectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val itemList = ArrayList<Item>()

                    for (document in querySnapshot.documents) {
                        // Map the document fields to your Item data class
                        val id = document.getString("id") ?: ""
                        val name = document.getString("name") ?: ""
                        val released = document.getString("released") ?: ""
                        val rating = document.getString("rating") ?: ""
                        val background_image = document.getString("background_image") ?: ""
                        val quantity = document.getString("quantity") ?: ""
                        val price = document.getString("price") ?: ""


                        // Create a new Item object and add it to the list
                        val item = Item(
                            id,
                            name.lowercase(),
                            released,
                            rating,
                            background_image,
                            quantity,
                            price
                        )

                        ItemManager.add(item)
                    }


                    binding.recycler.adapter =
                        FireBase_ItemAdapter(
                            ItemManager.items,
                            object : FireBase_ItemAdapter.ItemListener {

                                override fun onItemClicked(index: Int) {

                                    Toast.makeText(
                                        requireContext(),
                                        "${ItemManager.items[index]}", Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(
                                        R.id.action_customer_buy_ItemsFragment_to_buying_Item,
                                        bundleOf("item" to index)
                                    )
                                    ///////////////////////////////////////////////////////////////////////////////////////////////

                                }

                                override fun onItemLongClicked(index: Int) {
                                    //TODO item detail maybe can connect to detail

                                }
                            })


                }

                .addOnFailureListener { exception ->
                    // Error occurred while retrieving the collection
                }
        }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())


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
        if (item.itemId == R.id.logoutBtn){
            firebaseAuth.signOut()
            Navigation.findNavController(binding.root).navigate(R.id.action_customer_buy_ItemsFragment_to_login)
        }
        return super.onOptionsItemSelected(item)
    }
}
