package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.ada.mybuffet.R
import com.ada.mybuffet.screens.home.Home

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var register = findViewById<Button>(R.id.login_bT_registerMail)
        var login = findViewById<Button>(R.id.login_bT_logIn)

        // live data callback:
        // var textfield = findViewById<TextView>(R.id.login_bT_logIn)
        // if (blabla) {
        //    textfield.setTextColor()
        //}

        register.setOnClickListener {
           var i = Intent(this,Register::class.java)
           startActivity(i)
        }

        login.setOnClickListener {
            var i = Intent(this,Home::class.java)
            startActivity(i)
            finish()
        }
    }
}