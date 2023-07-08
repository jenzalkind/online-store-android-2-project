package com.example.online_store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.online_store.databinding.ApiFragmentAllItemsBinding
import com.google.firebase.firestore.FirebaseFirestore


class Api_All_Items_Fragment : Fragment() {

    val fireStoreDatabase = FirebaseFirestore.getInstance()

    private var _binding : ApiFragmentAllItemsBinding?=null
    private val binding get() = _binding!!



    var itemList = arrayListOf<Item>()
    val apiSample2 = "https://api.rawg.io/api/games?key=aec8bf799fee4ab8bdff28649a063f72&page=1"
    var recyclerView: RecyclerView? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ApiFragmentAllItemsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        for (k in 1 until 6)
        {

            val apiSample = apiSample2.plus(k.toString())

            val reqQueue: RequestQueue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(Request.Method.GET,apiSample, null, { res ->
                //Log.d("Volley Sample", res.getString("page"))


                val jsonArray = res.getJSONArray("results")
                for (i in 0 until jsonArray.length()){
                    val jsonObj = jsonArray.getJSONObject(i)


                    var quantity= RandomQuantity()
                    var price = RandomPrice()


                    val item = Item(
                        jsonObj.getInt("id").toString(),
                        jsonObj.getString("name").lowercase(),
                        jsonObj.getString("released"),
                        jsonObj.getString("rating"),
                        //jsonObj.getString("genres"),
                        jsonObj.getString("background_image"),
                        quantity.toString(),
                        price.toString()

                    )

                    itemList.add(item)
                }

                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = Api_ItemAdapter(itemList)

                Log.d("itemList", itemList.toString())


            }, {err ->
                Log.d("Volley Sample Fail", err.message.toString())
            })

            reqQueue.add(request)


            Log.d("reqQueue", reqQueue.toString())


        }




        binding.button.setOnClickListener()
        {

            for (i in 0 until itemList.size-1) {
                fireStoreDatabase.collection("Item").add(itemList[i]).addOnSuccessListener {
                    Log.d(tag,"yes${i}")
                }.addOnFailureListener()
                {
                    Log.d(tag,"no${i}")

                }
            }


        }

        binding.button2.setOnClickListener()
        {


            val collectionRef = fireStoreDatabase.collection("Item")

            collectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = fireStoreDatabase.batch()
                    for (document in querySnapshot.documents) {
                        batch.delete(document.reference)
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
            findNavController().navigate(R.id.action_api_All_Items_Fragment_to_admin_Fragment)



        }





    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}