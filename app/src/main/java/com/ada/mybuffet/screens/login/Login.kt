package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.ada.mybuffet.R
import com.ada.mybuffet.screens.home.Home
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_bT_registerMail.setOnClickListener {
            // go back to calling activity
            finish()
        }

        login_bT_logIn.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = login_et_email.text.toString()
        val password = login_et_password.text.toString()

        val i = Intent(this, Home::class.java)
        startActivity(i)
        finish()
    }
}