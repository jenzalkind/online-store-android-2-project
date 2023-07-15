package com.example.online_store

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.online_store.databinding.ApiNewFragmentItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar


class ApiNew_ItemFragment : Fragment() {

    val fireStoreDatabase = FirebaseFirestore.getInstance()


    private var _binding : ApiNewFragmentItemBinding?=null
    private val binding get() = _binding!!

    private var c : Calendar = Calendar.getInstance()
    private var year = c.get(Calendar.YEAR)
    private var month =  c.get(Calendar.MONTH)
    private var day =  c.get(Calendar.DAY_OF_MONTH)
    var date_string=""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ApiNewFragmentItemBinding.inflate(inflater,container,false)


        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //arguments?.getInt("item")?.let {


            val item = chosenItem.getGame()//ItemManager.items[it]


            binding.name20.setText(item.name)

            binding.dateBtn20.text = item.released
            binding.rating20.text = item.rating

            binding.price20.setText("0")
            binding.quantity20.setText("0")


            Glide.with(requireContext()).load(item.background_image).circleCrop()
                .into(binding.resultImage20)

            date_string = binding.dateBtn20.text.toString()

            binding.Add20.setOnClickListener {

                runBlocking {
                    launch {
                        try {
                            val item = Item(

                                item.id,
                                item.name.lowercase(),
                                item.released,
                                item.rating.toString(),
                                item.background_image,
                                binding.quantity20.text.toString(),
                                binding.price20.text.toString()

                            )

                            fireStoreDatabase.collection("Item").add(item).addOnSuccessListener {
                                Log.d(tag,"yes${item}")
                            }.addOnFailureListener()
                            {
                                Log.d(tag,"no${item}")

                            }


                        } catch (e: Exception) {
                            // Handle any exceptions
                            e.printStackTrace()

                        }
                    }
                }


                findNavController().navigate(R.id.action_apiNew_ItemFragment_to_api_All_Items_Fragment)


            }

       // }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}


