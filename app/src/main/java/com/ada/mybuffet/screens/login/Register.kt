package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ada.mybuffet.R
import com.ada.mybuffet.screens.home.Home
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_bT_logIn.setOnClickListener {
            val i = Intent(this, Home::class.java)
            startActivity(i)
            finish()
        }

        register_tv_go_to_login.setOnClickListener {
            val i = Intent(this, Login::class.java)
            startActivity(i)
        }
    }
}