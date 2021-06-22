package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ada.mybuffet.databinding.ActivityLoginBinding
import com.ada.mybuffet.screens.home.Home

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
    }

    private fun performLogin() {
        val email = binding.loginEtEmail.text.toString()
        val password = binding.loginEtPassword.text.toString()

        val i = Intent(this, Home::class.java)
        startActivity(i)
        finish()
    }
}