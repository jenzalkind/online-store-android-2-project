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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentSignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupFrag : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var database: DatabaseReference
    private lateinit var arrow: ImageView
    private lateinit var alertLoadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        firstNameInput = binding.fName
        lastNameInput = binding.lName
        emailInput = binding.email
        passwordInput = binding.password
        confirmPasswordInput = binding.cnfPassword
        arrow = binding.arrowback
        firebaseAuth = FirebaseAuth.getInstance()

        onCLickSignUpListener(binding.signBtn)
        backArrowListener()
        return binding.root
    }

    private fun onCLickSignUpListener(signUpBtn: Button) {
        signUpBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.loading_item, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            alertLoadingDialog = builder.create()

            registerUser()
        }
    }

    private fun registerUser() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confPassword = confirmPasswordInput.text.toString().trim()

        when {
            TextUtils.isEmpty(firstName) -> {
                firstNameInput.requestFocus()
                firstNameInput.error = "Please enter your first name"
            }

            TextUtils.isEmpty(lastName) -> {
                lastNameInput.requestFocus()
                lastNameInput.error = "Please enter your last name"
            }

            TextUtils.isEmpty(email) -> {
                emailInput.requestFocus()
                emailInput.error = "Please enter your email"
            }

            (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                emailInput.requestFocus()
                emailInput.error = "Invalid email format"
            }

            TextUtils.isEmpty(password) -> {
                passwordInput.requestFocus()
                passwordInput.error = "Please enter your password"
            }

            TextUtils.isEmpty(confPassword) || (password != confPassword) -> {
                confirmPasswordInput.requestFocus()
                confirmPasswordInput.error = "Please repeat your password"
            }

            firstName.isNotEmpty() && lastName.isNotEmpty() && password.isNotEmpty() && confPassword.isNotEmpty() -> {
                alertLoadingDialog.show()
                firebaseSignUp(firstName, lastName, email, password)
            }
        }
    }

    private fun firebaseSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        binding.signBtn.isEnabled = false
        binding.signBtn.alpha = 0.5f

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                database = FirebaseDatabase.getInstance().getReference("Users")
                val user = NewUser(firstName, lastName, email, password)
                database.child("users").push().setValue(user).addOnSuccessListener {
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show()
                }
                findNavController().navigate(R.id.action_sign_up_fregment_to_customer_FireBase_AllItemsFragment)

            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }

            binding.signBtn.isEnabled = true
            binding.signBtn.alpha = 1.0f

            alertLoadingDialog.dismiss()
        }
    }

    private fun backArrowListener() {
        arrow.setOnClickListener {
            findNavController().navigate(R.id.action_back_sign_up_to_login_Fragment)
        }
    }
}