package com.example.online_store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentAdminBinding
import com.example.online_store.databinding.ApiFragmentAllItemsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class Admin_Fragment : Fragment() {

    private var _binding : FragmentAdminBinding?=null
    private val binding get() = _binding!!


    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Item")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(inflater,container,false)




        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_admin_Fragment_to_api_All_Items_Fragment)

        }
        binding.button4.setOnClickListener {
            ItemManager.items.clear()



            findNavController().navigate(R.id.action_admin_Fragment_to_fireBase_AllItemsFragment)

        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}