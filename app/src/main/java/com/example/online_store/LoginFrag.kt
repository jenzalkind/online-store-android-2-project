package com.example.online_store

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFrag : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var alertLoadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        val currentEmail = FirebaseAuth.getInstance().currentUser?.email?.trim()
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            when (currentEmail) {
                "admin@gmail.com" -> findNavController().navigate(R.id.action_loginFragment_to_admin_Fragment)
                else -> findNavController().navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        emailInput = binding.usernameLoginET
        passwordInput = binding.passwordLoginET
        firebaseAuth = FirebaseAuth.getInstance()

        onClickLoginListener(binding.loginBtn)
        onClickSignUpListener()

        return binding.root
    }

    private fun onClickLoginListener(loginBtn: Button) {
        loginBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.loading_item, null)
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            builder.setView(dialogView)
            builder.setCancelable(false)
            alertLoadingDialog = builder.create()

            when {
                TextUtils.isEmpty(email) -> {
                    emailInput.requestFocus()
                    emailInput.error = "Please enter your email"
                }

                TextUtils.isEmpty(password) -> {
                    passwordInput.requestFocus()
                    passwordInput.error = "Please enter your password"
                }

                email.isNotEmpty() && password.isNotEmpty() -> {
                    alertLoadingDialog.show()
                    firebaseLogin(email, password)
                }
            }
        }
    }

    private fun firebaseLogin(email: String, password: String) {
        binding.loginBtn.isEnabled = false
        binding.loginBtn.alpha = 0.5f

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                when (email) {
                    "admin@gmail.com" -> {
                        Toast.makeText(
                            context,
                            "Welcome Admin, have a nice day",
                            Toast.LENGTH_SHORT
                        ).show()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_loginFragment_to_admin_Fragment)
                    }

                    else -> Navigation.findNavController(binding.root)
                        .navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)
                }
            } else {
                Toast.makeText(context, "Email and password don't match", Toast.LENGTH_SHORT).show()
            }

            alertLoadingDialog.dismiss()

            binding.loginBtn.isEnabled = true
            binding.loginBtn.alpha = 1.0f
        }
    }

    private fun onClickSignUpListener() {
        binding.signUp.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_sign_up_fregment)
        }
    }
}