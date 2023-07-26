package com.example.online_store

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFrag : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var isdialog: AlertDialog
    //val userEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val current_email =FirebaseAuth.getInstance().currentUser?.email?.trim()


        if (FirebaseAuth.getInstance().currentUser != null) {



            when (current_email) {
                "admin@gmail.com" ->findNavController().navigate(R.id.action_loginFragment_to_admin_Fragment)
                else -> findNavController().navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        username = binding.usernameLoginET
        password = binding.passwordLoginET
        firebaseAuth = FirebaseAuth.getInstance()

        setLoginListener(binding.loginBtn)
        setSignUpListener()

        return binding.root
    }

    private fun setLoginListener(loginBtn: Button) {
        binding.loginBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.loading_item, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            isdialog = builder.create()
            isdialog.show()
            loginUser()
        }
    }

    private fun loginUser() {
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_warning)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.error = R.string.warning1.toString()
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.error = R.string.warning2.toString()
            }
            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() -> {
                firebaseLoginIn()
            }
        }
    }

    private fun firebaseLoginIn() {
        binding.loginBtn.isEnabled = false
        binding.loginBtn.alpha = 0.5f

        firebaseAuth.signInWithEmailAndPassword(
            username.text.toString().trim(),
            password.text.toString().trim()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isdialog.dismiss()

                when (username.text.toString().trim()) {
                    "admin@gmail.com" ->Navigation.findNavController(binding.root)
                        .navigate(R.id.action_loginFragment_to_admin_Fragment)
                    else -> Navigation.findNavController(binding.root)
                        .navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)
                }


            } else {
                isdialog.dismiss()
                binding.loginBtn.isEnabled = true
                binding.loginBtn.alpha = 1.0f
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSignUpListener() {
        binding.signUp.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_sign_up_fregment)
        }
    }







}
