package com.example.online_store

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.AddItemLayoutBinding
import java.util.Calendar


import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


//////////// need to add to fire base


class Add_ItemFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()


    private var _binding : AddItemLayoutBinding? = null

    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    val pickImageLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.resultImage.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(it,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
        }



    private var c : Calendar = Calendar.getInstance()
    private var year = c.get(Calendar.YEAR)
    private var month =  c.get(Calendar.MONTH)
    private var day =  c.get(Calendar.DAY_OF_MONTH)
    var date_flag =0
    var date_string=""




    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddItemLayoutBinding.inflate(inflater,container,false)


        binding.imageBtn2.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.dateBtn.setOnClickListener{
            val dpd = DatePickerDialog(requireContext(), { view, mYear, mMonth, mDay ->
                binding.dateBtn.text = "$mDay/"+(mMonth+1).toString()+"/$mYear"
                year = mYear
                month = mMonth
                day = mDay
            },year,month,day)
            //date_string


            dpd.datePicker.minDate= c.timeInMillis



            dpd.show()

            dpd.setOnDateSetListener{ view, mYear, mMonth, mDay ->
                date_flag =1
                binding.dateBtn.text = "$mDay/"+(mMonth+1).toString()+"/$mYear"
                year = mYear
                month = mMonth
                day = mDay
                date_string="$mDay/"+(mMonth+1).toString()+"/$mYear"
            }
        }



        ///////////////////////////////////////////////////////////////


        binding.save.setOnClickListener {
            /*val bundle = bundleOf("title" to binding.itemTitle.text.toString(), "description" to binding.itemDescription.text.toString())
            findNavController().navigate(R.id.action_addItemFragment_to_allItemsFragment,bundle)
        */

            if (date_flag==0) {
                Toast.makeText(requireContext(), "release today", Toast.LENGTH_SHORT).show()
                date_string = getCurrentDate()

            }


            val item  = Item(
                id= UUID.randomUUID().toString(),
                binding.name2.text.toString().lowercase(),
                date_string,
                binding.rating2.text.toString(),
                imageUri.toString(),
                binding.quantity2.text.toString(),
                binding.price2.text.toString()


            )

            if (binding.name2.text.toString().lowercase()=="") {
                showSimpleDialog()
            }
            else {
                ItemManager.add(item)



                db.collection("Item").add(item)


                findNavController().navigate(R.id.action_add_ItemFragment_to_fireBase_AllItemsFragment)
            }

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

///////////////////////
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("error")
        builder.setMessage("the game must have name")

        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            // Do something when the user clicks the "OK" button (optional)
        }

        // If you want to handle a negative button (optional)
        /*builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            // Do something when the user clicks the "Cancel" button (optional)
        }*/

        val dialog = builder.create()
        dialog.show()
    }


}