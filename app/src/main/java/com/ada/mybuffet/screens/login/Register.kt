package com.ada.mybuffet.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.ActivityRegisterBinding
import com.ada.mybuffet.screens.home.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.registerBTLogIn.setOnClickListener {
            performRegistration()
        }

        binding.registerTvGoToLogin.setOnClickListener {
            val i = Intent(this, Login::class.java)
            startActivity(i)
        }
    }

    private fun performRegistration() {
        // get values of text fields
        val email = binding.registerEtEmail.text.toString()
        val password = binding.registerEtPassword.text.toString()
        val passwordRepeated = binding.registerEtRepeatPassword.text.toString()

        if (email.isEmpty() || password.isEmpty() || passwordRepeated.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_fields_not_filled), Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordRepeated) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_passwords_dont_match), Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6 || password.length > 40) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_password_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        // create user in firebase auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                storeUserInDatabase(email, it.user?.uid)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.register_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun storeUserInDatabase(email: String, uid: String?) {
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_failure_message), Toast.LENGTH_SHORT).show()
            return
        }

        // get firestore instance
        val firestore = Firebase.firestore

        // Create a new user as a key value data structure
        val user = hashMapOf(
            "authuid" to uid,
            "email" to email
        )

        // store user in firestore
        firestore.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("RegisterScreen", "DocumentSnapshot added with ID: ${documentReference.id}")
                closeActivityAndGoToHome()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.register_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }


    private fun closeActivityAndGoToHome() {
        val i = Intent(this, Home::class.java)
        startActivity(i)
        finish()
    }
}