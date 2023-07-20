package com.example.online_store

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.online_store.databinding.FragmentAdminBinding
import com.example.online_store.databinding.FragmentEditItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.UUID


class Edit_ItemFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()


    private var _binding : FragmentEditItemBinding?=null
    private val binding get() = _binding!!

    private var c : Calendar = Calendar.getInstance()
    private var year = c.get(Calendar.YEAR)
    private var month =  c.get(Calendar.MONTH)
    private var day =  c.get(Calendar.DAY_OF_MONTH)
    var date_string=""

    var imge_on=""

    val collectionRef = db.collection("Item")



    var the_one :Int =0

    private var imageUri: Uri? = null

    val pickImageLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.resultImage.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
        }



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
            the_one =it
            val item = ItemManager.items[it]


            binding.name2.setText(item.name)

            binding.dateBtn.text = item.released
            binding.rating2.setText(item.rating)

            binding.price2.setText(item.price)
            binding.quantity2.setText(item.quantity)


            Glide.with(requireContext()).load(item.background_image).circleCrop()
                .into(binding.resultImage)


            date_string = binding.dateBtn.text.toString()

            binding.imageBtn2.setOnClickListener {
                pickImageLauncher.launch(arrayOf("image/*"))
            }

            binding.save.setOnClickListener {

                if ((imageUri==null)||(imageUri.toString()==""))
                {
                    imge_on=item.background_image

                }
                else{
                    imge_on=imageUri.toString()

                }

                val updatedItem   = Item(
                    id= ItemManager.items[the_one].id,
                    binding.name2.text.toString().lowercase(),
                    binding.dateBtn.text as String,
                    binding.rating2.text.toString(),
                    imge_on,
                    binding.quantity2.text.toString(),
                    binding.price2.text.toString()


                )

                updateItemsByName(binding.name2.text.toString().lowercase(), updatedItem)


                findNavController().navigate(R.id.action_edit_ItemFragment_to_fireBase_AllItemsFragment)


            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }








}


