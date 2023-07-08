package com.example.online_store


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var resetButton: MaterialButton
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_pass, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        emailEditText = view.findViewById(R.id.emailEditText)
        resetButton = view.findViewById(R.id.resetButton)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString()

            // Call the reset password function with the entered email
            resetPassword(email)
        }


        return view
    }
                private fun resetPassword(email: String) {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Password reset email sent successfully
                                Toast.makeText(requireContext(), "Password reset email sent!", Toast.LENGTH_SHORT).show()
                                // Handle success case (e.g., show a success message)
                            } else {
                                // Password reset email failed to send
                                val errorMessage = task.exception?.message ?: "Failed to send password reset email!"
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                // Handle failure case (e.g., show an error message)
                            }
                        }
                }

            }


