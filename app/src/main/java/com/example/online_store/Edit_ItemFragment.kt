package com.example.online_store

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


    var price_add_flag =0


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



            dpd.datePicker.maxDate = c.timeInMillis



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


                if (binding.name2.text.toString().lowercase() != "") {


                    if ((imageUri == null) || (imageUri.toString() == "")) {
                        imge_on = item.background_image

                    } else {
                        imge_on = imageUri.toString()

                    }

                    val rating_add = convertToNumberOrZero(binding.rating2.text.toString())
                    val quantity_add = convertToNumberOrZero(binding.quantity2.text.toString())
                    val price_add = convertToNumberOrZero(binding.price2.text.toString())


                    if (price_add == "0"&&price_add_flag==0) {
                        showSimpleDialog3()

                    } else {
                        price_add_flag = 1
                    }


                    if (price_add_flag == 1) {
                        if (quantity_add.toInt() >= 1) {
                            val updatedItem = Item(
                                id = ItemManager.items[the_one].id,
                                binding.name2.text.toString().lowercase(),
                                binding.dateBtn.text as String,
                                rating_add,
                                imge_on,
                                quantity_add,
                                price_add


                            )

                            updateItemsByName(
                                binding.name2.text.toString().lowercase(),
                                updatedItem
                            )


                            findNavController().navigate(R.id.action_edit_ItemFragment_to_fireBase_AllItemsFragment)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.there_must_be_at_least_one_game),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }else {
                    showSimpleDialog()
                }


            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.error))
        builder.setMessage(getString(R.string.the_game_must_have_name))

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
        }


        val dialog = builder.create()
        dialog.show()
    }



    private fun showSimpleDialog3() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.check))
        builder.setMessage(getString(R.string.are_you_sure_you_want_the_game_to_cost_0))

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            price_add_flag=1

        }
        // If you want to handle a negative button (optional)
        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            // Do something when the user clicks the "Cancel" button (optional)
            price_add_flag=0
        }



        val dialog = builder.create()
        dialog.show()
    }




}


