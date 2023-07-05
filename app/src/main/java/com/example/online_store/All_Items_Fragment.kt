package com.example.online_store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.easylearn.User
import com.example.easylearn.UserAdapter


class All_Items_Fragment : Fragment() {

    var userList = arrayListOf<User>()
    val apiSample2 = "https://api.rawg.io/api/games?key=aec8bf799fee4ab8bdff28649a063f72&page=1"
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_all__items_, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView =  view.findViewById(R.id.recyclerView)

        for (k in 0 until 5)
        {

            val apiSample = apiSample2.plus(k.toString())

            val reqQueue: RequestQueue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(Request.Method.GET,apiSample, null, { res ->
                //Log.d("Volley Sample", res.getString("page"))


                val jsonArray = res.getJSONArray("results")
                for (i in 0 until jsonArray.length()){
                    val jsonObj = jsonArray.getJSONObject(i)

                    val user = User(
                        jsonObj.getInt("id"),
                        jsonObj.getString("name"),
                        jsonObj.getString("released"),
                        jsonObj.getString("rating"),
                        //jsonObj.getString("genres"),
                        jsonObj.getString("background_image")
                    )

                    userList.add(user)
                }

                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = UserAdapter(userList)

                Log.d("userList", userList.toString())


            }, {err ->
                Log.d("Volley Sample Fail", err.message.toString())
            })

            reqQueue.add(request)


            Log.d("reqQueue", reqQueue.toString())


        }

    }

}