package com.example.online_store

import android.os.Bundle
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


import com.example.online_store.databinding.FireBaseAllItemsLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore


/////// FireBase


class FireBase_AllItemsFragment : Fragment() {

    private var _binding : FireBaseAllItemsLayoutBinding? = null

    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Item")



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FireBaseAllItemsLayoutBinding.inflate(inflater,container,false)

        binding.fab.setOnClickListener {

            //ItemManager.items.clear()
            findNavController().navigate(R.id.action_fireBase_AllItemsFragment_to_add_ItemFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }



        //ItemManager.items.clear()//////////#######################
/*        binding.fab2.setOnClickListener {
            ItemManager.items.clear()

            findNavController().navigate(R.id.action_edit_AllItemsFragment_to_admin_Fragment)


        }*/


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
                        name,
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
                            findNavController().navigate(
                                R.id.action_fireBase_AllItemsFragment_to_detail_ItemFragment,
                                bundleOf("item" to index)
                            )

                        }

                        override fun onItemLongClicked(index: Int) {
                            findNavController().navigate(
                                R.id.action_fireBase_AllItemsFragment_to_edit_ItemFragment,
                                bundleOf("item" to index)
                            )
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

  /*              if (deletedItem!=null)
                {
                    deleteItemById("Item", deletedItem) { isSuccess ->
                        if (isSuccess) { }
                        else { }
                    }
                }*/




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




    private fun deleteItemById(collectionName: String, itemId: String, onComplete: (Boolean) -> Unit) {
        val docRef = db.collection(collectionName).document(itemId)
        docRef.delete()
            .addOnSuccessListener {
                println("Document successfully deleted!")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
                onComplete(false)
            }

    }
}
