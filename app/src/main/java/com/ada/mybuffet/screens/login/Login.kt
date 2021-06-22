package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.ActivityLoginBinding
import com.ada.mybuffet.screens.home.Home
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginBTRegisterMail.setOnClickListener {
            // go back to calling activity
            finish()
        }

        binding.loginBTLogIn.setOnClickListener {
            performLogin()
        }

        binding.loginTvResetPassword.setOnClickListener {
            val i = Intent(this, ResetPassword::class.java)
            startActivity(i)
        }
    }

    private fun performLogin() {
        val email = binding.loginEtEmail.text.toString()
        val password = binding.loginEtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_fields_not_filled), Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val i = Intent(this, Home::class.java)
                startActivity(i)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.login_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }
}