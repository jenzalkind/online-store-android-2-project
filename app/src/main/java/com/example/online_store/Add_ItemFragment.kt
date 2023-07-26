package com.example.online_store

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentResolver
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

    var price_add_flag =0




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


            dpd.datePicker.maxDate= c.timeInMillis



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







        binding.save.setOnClickListener {


            if (date_flag==0) {
                Toast.makeText(requireContext(), getString(R.string.release_today), Toast.LENGTH_SHORT).show()
                date_string = getCurrentDate()

            }




            if (binding.name2.text.toString().lowercase()=="") {
                showSimpleDialog()
            }

            if (binding.name2.text.toString().lowercase()!="") {


                val rating_add =convertToNumberOrZero(binding.rating2.text.toString())
                val quantity_add =convertToNumberOrZero(binding.quantity2.text.toString())
                val price_add =convertToNumberOrZero(binding.price2.text.toString())





                if (imageUri.toString()=="null") {
                    val resourceId = R.drawable.no_game
                    val uri = Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(resources.getResourcePackageName(resourceId))
                        .appendPath(resources.getResourceTypeName(resourceId))
                        .appendPath(resources.getResourceEntryName(resourceId))
                        .build()
                    imageUri=uri
                    Toast.makeText(requireContext(),getString(R.string.setting_default_image), Toast.LENGTH_SHORT).show()
                }


                if(price_add=="0"&&price_add_flag==0)
                {
                    showSimpleDialog3()

                }
                else
                {
                    price_add_flag=1
                }


                if(price_add_flag==1) {
                    if (quantity_add.toInt() >= 1) {
                        val item = Item(
                            id = UUID.randomUUID().toString(),
                            binding.name2.text.toString().lowercase(),
                            date_string,
                            rating_add,
                            imageUri.toString(),
                            quantity_add,
                            price_add


                        )


                        ItemManager.add(item)



                        db.collection("Item").add(item)


                        findNavController().navigate(R.id.action_add_ItemFragment_to_fireBase_AllItemsFragment)
                    } else {
                        Toast.makeText(requireContext(),getString(R.string.there_must_be_at_least_one_game), Toast.LENGTH_SHORT).show()
                    }
                }


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