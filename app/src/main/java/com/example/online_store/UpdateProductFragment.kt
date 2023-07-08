package com.example.online_store


    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import com.google.android.material.button.MaterialButton
    import com.google.android.material.textfield.TextInputEditText
    import com.google.firebase.FirebaseOptions
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.ktx.Firebase

class UpdateProductFragment : Fragment() {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var productPriceEditText: TextInputEditText
    private lateinit var updateButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var productId: String
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.updateobject, container, false)

        var Firestore = FirebaseAuth.getInstance()

        productNameEditText = view.findViewById(R.id.productNameEditText)
        productPriceEditText = view.findViewById(R.id.productPriceEditText)
        updateButton = view.findViewById(R.id.updateButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        updateButton.setOnClickListener {
            val name = productNameEditText.text.toString()
            val price = productPriceEditText.text.toString()
            val option  = FirebaseOptions.Builder()


            // Call the update function with the entered details
            updateProduct(name, price)


        }

        deleteButton.setOnClickListener {
            // Call the delete function
            deleteProduct()
        }

        // Retrieve the product ID passed from the previous screen
        val arguments = arguments
        if (arguments != null) {
            productId = arguments.getString(PRODUCT_ID_KEY, "")
        }

        // Fetch the existing product details from Firestore
        fetchProductDetails()

        return view
    }

    private fun fetchProductDetails() {
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: ""
                    val price = documentSnapshot.getDouble("price") ?: 0.0

                    // Display the existing product details in the EditText fields
                    productNameEditText.setText(name)
                    productPriceEditText.setText(price.toString())
                } else {
                    // Handle the case where the product does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur while fetching the product details
            }
    }

    private fun updateProduct(name: String, price: String) {
        firestore.collection("products").document(productId)
            .update(
                "name", name,
                "price", price.toDoubleOrNull()
            )
            .addOnSuccessListener {
                // Handle the successful update (e.g., show a success message)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the update
            }
    }

    private fun deleteProduct() {
        firestore.collection("products").document(productId)
            .delete()
            .addOnSuccessListener {
                // Handle the successful deletion (e.g., show a success message)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the deletion
            }
    }

    companion object {
        private const val PRODUCT_ID_KEY = "productId"

        fun newInstance(productId: String): UpdateProductFragment {
            val fragment = UpdateProductFragment()
            val arguments = Bundle()
            arguments.putString(PRODUCT_ID_KEY, productId)
            fragment.arguments = arguments
            return fragment
        }
    }
}




















