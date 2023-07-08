package com.example.online_store

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.online_store.databinding.FragmentAdminBinding
import com.example.online_store.databinding.FragmentEditItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar


class Edit_ItemFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()


    private var _binding : FragmentEditItemBinding?=null
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
        _binding = FragmentEditItemBinding.inflate(inflater,container,false)

        binding.dateBtn.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), { view, mYear, mMonth, mDay ->
                binding.dateBtn.text = "$mDay/" + (mMonth + 1).toString() + "/$mYear"
                year = mYear
                month = mMonth
                day = mDay
            }, year, month, day)



            dpd.datePicker.minDate = c.timeInMillis



            dpd.show()
        }


        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("item")?.let {
            val item = ItemManager.items[it]


            binding.name2.setText(item.name)

            binding.dateBtn.text = item.released
            binding.rating2.setText(item.rating)

            binding.price2.setText(item.price)
            binding.quantity2.setText(item.quantity)


            Glide.with(requireContext()).load(item.background_image).circleCrop()
                .into(binding.resultImage)

            date_string = binding.dateBtn.text.toString()

            binding.save.setOnClickListener {

                runBlocking {
                    launch {
                        try {
                            deleteAndAddItem(

                                item.id,
                                binding.name2.text.toString().lowercase(),
                                date_string,
                                binding.rating2.text.toString(),
                                item.background_image,
                                binding.quantity2.text.toString(),
                                binding.price2.text.toString()

                            )



                        } catch (e: Exception) {
                            // Handle any exceptions
                            e.printStackTrace()

                        }
                    }
                }


                findNavController().navigate(R.id.action_edit_ItemFragment_to_fireBase_AllItemsFragment)




                /*
                var romin_id= item.id


                val collectionRef = db.collection("Item")

                collectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {
                            if(document.getString("id")==romin_id ) {
                                batch.delete(document.reference)
                            }
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                // Collection cleared successfully



                                var item  = Item(
                                    id= romin_id,
                                    binding.name2.text.toString(),
                                    date_string,
                                    binding.rating2.text.toString(),
                                    item.background_image,
                                    binding.quantity2.text.toString(),
                                    binding.price2.text.toString()


                                )
                                ItemManager.add(item)



                                db.collection("Item").add(item)

                                findNavController().navigate(R.id.action_edit_AllItemsFragment_to_detailItemFragment)
                            }
                            .addOnFailureListener { exception ->
                                // Error occurred while clearing the collection
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Error occurred while fetching documents from the collection
                    }


                        //////////////////
                        /////////////// need to add to fire base
                        ///////////////


                }





     */
            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}


