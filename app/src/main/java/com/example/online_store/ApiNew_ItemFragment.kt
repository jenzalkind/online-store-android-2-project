package com.example.online_store

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


    var price_api_flag=0


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


        binding.name20.text = item.name

        binding.dateBtn20.text = item.released
        binding.rating20.text = item.rating

        binding.price20.setText("0")
        binding.quantity20.setText("0")


        binding.originalPrice.text = item.price +" $"



        Glide.with(requireContext()).load(item.background_image).circleCrop()
            .into(binding.resultImage20)

        date_string = binding.dateBtn20.text.toString()

        binding.Add20.setOnClickListener {

            runBlocking {
                launch {
                    try {

                        val price_api= convertToNumberOrZero(binding.price20.text.toString())

                        val quantity_api= convertToNumberOrZero(binding.quantity20.text.toString())

                        if(price_api=="0"&&price_api_flag==0)
                        {
                            showSimpleDialog3()

                        }
                        else
                        {
                            price_api_flag=1
                        }


                        if(price_api_flag==1)
                        {
                            if(quantity_api.toInt()>=1) {


                                val item = Item(

                                    item.id,
                                    item.name.lowercase(),
                                    item.released,
                                    item.rating.toString(),
                                    item.background_image,
                                    binding.quantity20.text.toString(),
                                    binding.price20.text.toString()

                                )

                                fireStoreDatabase.collection("Item").add(item)
                                    .addOnSuccessListener {
                                        Log.d(tag, "yes${item}")

                                        findNavController().navigate(R.id.action_apiNew_ItemFragment_to_api_All_Items_Fragment)

                                    }.addOnFailureListener()
                                {
                                    Log.d(tag, "no${item}")

                                }
                            }
                            else
                            {
                                Toast.makeText(requireContext(),getString(R.string.there_must_be_at_least_one_game), Toast.LENGTH_SHORT).show()
                            }
                        }


                    } catch (e: Exception) {
                        // Handle any exceptions
                        e.printStackTrace()

                    }
                }
            }




        }

       // }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    private fun showSimpleDialog3() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.check))
        builder.setMessage(getString(R.string.are_you_sure_you_want_the_game_to_cost_0))

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            price_api_flag=1

        }
        // If you want to handle a negative button (optional)
        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            // Do something when the user clicks the "Cancel" button (optional)
            price_api_flag=0
        }



        val dialog = builder.create()
        dialog.show()
    }



}


