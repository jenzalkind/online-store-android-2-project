package com.example.online_store.Admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.online_store.databinding.ApiFragmentAllItemsBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.online_store.Item
import com.example.online_store.R
import com.example.online_store.RandomPrice
import com.example.online_store.RandomQuantity
import com.example.online_store.chosenItem
import com.google.firebase.auth.FirebaseAuth

class Api_All_Items_Fragment : Fragment(), Api_ItemAdapter.OnItemClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    val fireStoreDatabase = FirebaseFirestore.getInstance()

    private var _binding : ApiFragmentAllItemsBinding?=null
    private val binding get() = _binding!!

    //Key: f648ac0ab26c4a3b9dc164acf7773f57
    var itemList = arrayListOf<Item>()
    val apiSample2 = "https://api.rawg.io/api/games?key=f648ac0ab26c4a3b9dc164acf7773f57&page=1"
    var apiSample3="https://api.rawg.io/api/games?key=f648ac0ab26c4a3b9dc164acf7773f57&search="

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ApiFragmentAllItemsBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (k in 1 until 6) {
            val apiSample = apiSample2.plus(k.toString())
            val reqQueue: RequestQueue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(Request.Method.GET, apiSample, null, { res ->
                val jsonArray = res.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {
                    val jsonObj = jsonArray.getJSONObject(i)
                    val quantity = RandomQuantity()
                    val price = RandomPrice()
                    val item = Item(
                        jsonObj.getInt("id").toString(),
                        jsonObj.getString("name").lowercase(),
                        jsonObj.getString("released"),
                        jsonObj.getString("rating"),
                        jsonObj.getString("background_image"),
                        quantity.toString(),
                        price.toString()
                    )
                    itemList.add(item)
                }

                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = Api_ItemAdapter(itemList, this)
                Log.d("itemList", itemList.toString())
            }, { err ->
                Log.d(getString(R.string.volley_sample_fail), err.message.toString())
            })
            reqQueue.add(request)
            Log.d("reqQueue", reqQueue.toString())
        }
        binding.AddAllApiRes.setOnClickListener() {
            for (i in 0 until itemList.size - 1) {
                fireStoreDatabase.collection("Item").add(itemList[i]).addOnSuccessListener {
                    Log.d(tag, "yes${i}")
                }.addOnFailureListener()
                {
                    Log.d(tag, "no${i}")
                }
            }
        }

        binding.DelAllFirestormItems.setOnClickListener() {
            val collectionRef = fireStoreDatabase.collection("Item")
            collectionRef.get().addOnSuccessListener { querySnapshot ->
                val batch = fireStoreDatabase.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().addOnSuccessListener {
                    Log.d(tag, "Collection cleared successfully")
                }.addOnFailureListener {
                    Log.d(tag, "Error occurred while clearing the collection")

                }.addOnFailureListener {
                    Log.d(tag, "Error occurred while fetching documents from the collection")
                }
                findNavController().navigate(R.id.action_api_All_Items_Fragment_to_admin_Fragment)
            }
        }

        binding.searchButtonApi.setOnClickListener {
            itemList.clear()
            val apiSample = apiSample3 + binding.search.text
            val reqQueue: RequestQueue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(Request.Method.GET, apiSample, null, { res ->


                val jsonArray = res.getJSONArray("results")
                for (i in 0 until jsonArray.length()) {
                    val jsonObj = jsonArray.getJSONObject(i)
                    val quantity = RandomQuantity()
                    val price = RandomPrice()
                    val item = Item(
                        jsonObj.getInt("id").toString(),
                        jsonObj.getString("name").lowercase(),
                        jsonObj.getString("released"),
                        jsonObj.getString("rating"),

                        jsonObj.getString("background_image"),
                        quantity.toString(),
                        price.toString()
                    )
                    itemList.add(item)
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = Api_ItemAdapter(itemList, this)
                Log.d("itemList", itemList.toString())


            }, { err ->
                Log.d(getString(R.string.volley_sample_fail), err.message.toString())
            })
            reqQueue.add(request)
            Log.d("reqQueue", reqQueue.toString())
        }

    }

    //var apiSample="https://api.rawg.io/api/games?key=aec8bf799fee4ab8bdff28649a063f72&search="

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), itemList[position].name,Toast.LENGTH_SHORT).show()
        chosenItem.setGame(itemList[position])
        findNavController().navigate(R.id.action_api_All_Items_Fragment_to_apiNew_ItemFragment)
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
            Navigation.findNavController(binding.root).navigate(R.id.action_api_All_Items_Fragment_to_login)
        }
        return super.onOptionsItemSelected(item)
    }
}