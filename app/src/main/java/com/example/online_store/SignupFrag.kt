package com.example.online_store

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentSignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupFrag : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText
    private lateinit var database: DatabaseReference
    private lateinit var arrow: ImageView
    private lateinit var isdialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        fname = binding.fName
        lname = binding.lName
        email = binding.email
        password = binding.password
        cnfPassword = binding.cnfPassword
        arrow = binding.arrowback
        firebaseAuth = FirebaseAuth.getInstance()

        setSignUpBtnListener(binding.signBtn)
        setBackArrowListener()
        return binding.root
    }

    private fun setBackArrowListener(){
        arrow.setOnClickListener{
            findNavController().navigate(R.id.action_back_sign_up_to_login_Fragment)
        }
    }

    private fun setSignUpBtnListener(signUpBtn: Button) {
        signUpBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.loading_item,null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            isdialog = builder.create()
            isdialog.show()
            registerUser()
        }
    }

    private fun registerUser() {
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_warning)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        when {
            TextUtils.isEmpty(fname.text.toString().trim()) -> {
                binding.fName.error = R.string.warning3.toString()
            }
            TextUtils.isEmpty(lname.text.toString().trim()) -> {
                binding.lName.error = R.string.warning4.toString()
            }
            TextUtils.isEmpty(email.text.toString().trim()) -> {
                binding.email.error = R.string.warning1.toString()
            }
            (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches()) -> {
                //invalid email format
                binding.email.error = R.string.warning5.toString()
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                binding.password.error = R.string.warning2.toString()
            }
            TextUtils.isEmpty(cnfPassword.text.toString().trim()) -> {
                binding.cnfPassword.error = R.string.warning6.toString()
            }
            fname.text.toString().isNotEmpty() &&
                    lname.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() -> {
                if (password.text.toString() == cnfPassword.text.toString())
                    firebaseSignUp()
                else
                    binding.cnfPassword.error = R.string.warning7.toString()
            }
        }
    }

    private fun firebaseSignUp() {
        binding.signBtn.isEnabled = false
        binding.signBtn.alpha = 0.5f
        firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database = FirebaseDatabase.getInstance().getReference("Users")
                    val user = NewUser(fname.text.toString(),lname.text.toString(),email.text.toString(),password.text.toString())
                    database.child("users").push().setValue(user).addOnSuccessListener {
                        Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show()
                    }
                    isdialog.dismiss()
                    findNavController().navigate(R.id.action_sign_up_fregment_to_customer_FireBase_AllItemsFragment)

                } else {
                    isdialog.dismiss()
                    binding.signBtn.isEnabled = true
                    binding.signBtn.alpha = 1.0f
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}