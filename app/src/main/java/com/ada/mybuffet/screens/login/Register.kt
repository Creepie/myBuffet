package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ada.mybuffet.R
import com.ada.mybuffet.screens.home.Home
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_bT_logIn.setOnClickListener {
            performRegistration()
        }

        register_tv_go_to_login.setOnClickListener {
            val i = Intent(this, Login::class.java)
            startActivity(i)
        }
    }

    private fun performRegistration() {
        // get values of text fields
        val email = register_et_email.text.toString()
        val password = register_et_password.text.toString()
        val passwordRepeated = register_et_repeat_password.text.toString()

        // create user in firebase


        val i = Intent(this, Home::class.java)
        startActivity(i)
        finish()
    }
}