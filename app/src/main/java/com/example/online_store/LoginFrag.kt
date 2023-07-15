package com.example.online_store

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var forgotBtn:TextView
    private lateinit var isdialog:AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser!=null){
            findNavController().navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        username= binding.usernameLoginET
        password= binding.passwordLoginET
        forgotBtn = binding.forgottenpswBtn
        firebaseAuth = FirebaseAuth.getInstance()


        setLoginListener(binding.loginBtn)
        setSignUpListener()
        setForgotPswBtn()

        return binding.root
    }



    private fun setLoginListener(loginBtn: Button) {
        binding.loginBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.loading_item,null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            isdialog = builder.create()
            isdialog.show()
            loginUser()

        }

    }
    private fun setForgotPswBtn(){
        forgotBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.forget.toString())
            val view = layoutInflater.inflate(R.layout.fragment_forgot_pass,null)
            val user: EditText = view.findViewById(R.id.emailEditText)
            builder.setView(view)
            builder.setPositiveButton(R.string.reset.toString(), DialogInterface.OnClickListener { _, _ ->
                forgotPassword(user)
            })
            builder.setNegativeButton(R.string.close.toString(), DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }
    }

    private fun forgotPassword(userName:EditText) {
        when {
            TextUtils.isEmpty(userName.text.toString().trim()) -> {
                return
            }
            (!Patterns.EMAIL_ADDRESS.matcher(userName.text.toString().trim()).matches())->{
                //invalid email format
                return
            }
        }
        firebaseAuth.sendPasswordResetEmail(userName.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context,R.string.sent.toString(),Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser() {
        val icon = AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_warning)
        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when{
            TextUtils.isEmpty(username.text.toString().trim())->{
                username.error = R.string.warning1.toString()
            }
            TextUtils.isEmpty(password.text.toString().trim())->{
                password.error = R.string.warning2.toString()
            }

            username.text.toString().isNotEmpty()&&
                    password.text.toString().isNotEmpty()->{
                firebaseLoginIn()                    }
        }

    }

    private fun firebaseLoginIn() {
        binding.loginBtn.isEnabled = false
        binding.loginBtn.alpha = 0.5f

        firebaseAuth.signInWithEmailAndPassword(username.text.toString().trim(),password.text.toString().trim()).addOnCompleteListener {
                task->
            if(task.isSuccessful){
                isdialog.dismiss()
                findNavController(binding.root).navigate(R.id.action_loginFragment_to_customer_FireBase_AllItemsFragment)
                //findNavController(binding.root).navigate(R.id.action_loginFragment_to_customer_Fragment_buy)


            }else{
                isdialog.dismiss()
                binding.loginBtn.isEnabled = true
                binding.loginBtn.alpha = 1.0f
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()

            }

        }
    }


    private fun setSignUpListener() {
        binding.signUp.setOnClickListener { v -> findNavController(v).navigate(R.id.action_loginFragment_to_sign_up_fregment) }
    }



}