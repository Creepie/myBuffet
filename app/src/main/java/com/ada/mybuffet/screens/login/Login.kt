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
           val i = Intent(this, Register::class.java)
           startActivity(i)
        }

        login_bT_logIn.setOnClickListener {
            val i = Intent(this, Home::class.java)
            startActivity(i)
            finish()
        }
    }
}