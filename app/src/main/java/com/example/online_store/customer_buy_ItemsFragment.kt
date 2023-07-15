package com.example.online_store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.example.online_store.databinding.CustomerBuyItemsLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking


/////// FireBase


class customer_buy_ItemsFragment : Fragment() {

    private var _binding : CustomerBuyItemsLayoutBinding? = null

    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Item")



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomerBuyItemsLayoutBinding.inflate(inflater,container,false)

        binding.fab.setOnClickListener {

            //ItemManager.items.clear()
/*
            findNavController().navigate(R.id.action_fireBase_AllItemsFragment_to_add_ItemFragment)
*/
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////

        fun stringToNumber(input: String): Double? {
            return input.toDoubleOrNull()
        }

        var min = stringToNumber(binding.min.text.toString())
        var max = stringToNumber(binding.max.text.toString())

        var search_list: List<Item>

        binding.searchButton.setOnClickListener{
            runBlocking {



                search_list=searchItems(binding.search.text.toString(),binding.min.text.toString(),binding.max.text.toString())
                binding.recycler.adapter =
                    FireBase_ItemAdapter(search_list, object : FireBase_ItemAdapter.ItemListener {

                        override fun onItemClicked(index: Int) {

                            Toast.makeText(
                                requireContext(),
                                "${ItemManager.items[index]}", Toast.LENGTH_SHORT
                            ).show()
                           /* findNavController().navigate(
                                R.id.action_fireBase_AllItemsFragment_to_detail_ItemFragment,
                                bundleOf("item" to index)
                            )*/
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



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }





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
                    FireBase_ItemAdapter(ItemManager.items, object : FireBase_ItemAdapter.ItemListener {

                        override fun onItemClicked(index: Int) {

                            Toast.makeText(
                                requireContext(),
                                "${ItemManager.items[index]}", Toast.LENGTH_SHORT
                            ).show()
/*                            findNavController().navigate(
                                R.id.action_fireBase_AllItemsFragment_to_detail_ItemFragment,
                                bundleOf("item" to index)
                            )*/
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




            }

            .addOnFailureListener { exception ->
                // Error occurred while retrieving the collection
            }






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
                var deletedItem = ItemManager.items[position].id

                ItemManager.remove(viewHolder.adapterPosition)
                binding.recycler.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)






                val collectionRef = db.collection("Item")

                collectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {
                            if(document.getString("id")==deletedItem ) {
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
        }).attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
