package com.example.online_store

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.online_store.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : Fragment() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?{
        //binding = ActivitySignInBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        binding = ActivitySignInBinding.inflate(inflater,container,false)





        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {

            findNavController().navigate(R.id.action_signInActivity_to_signUpActivity)



        }


        binding.button5.setOnClickListener{

            firebaseAuth.signInWithEmailAndPassword("test@gmail.com","123456").addOnCompleteListener {
                if (it.isSuccessful) {
                    /*                        val intent = Intent(requireContext(), MainActivity::class.java)
                                            startActivity(intent)*/
                    findNavController().navigate(R.id.action_signInActivity_to_customer_FireBase_AllItemsFragment)

                } else {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()

                }
            }

        }



        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
/*                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)*/
                        findNavController().navigate(R.id.action_signInActivity_to_customer_FireBase_AllItemsFragment)

                    } else {
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }


        }

        return binding.root

    }


}