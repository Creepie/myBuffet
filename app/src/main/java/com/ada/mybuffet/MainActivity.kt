package com.ada.mybuffet

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ada.mybuffet.screens.home.Home
import com.ada.mybuffet.screens.login.Register
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStart() {
        super.onStart()

        val user = Firebase.auth.currentUser

        if (user != null) {
            // user is signed in --> show home activity
            val i = Intent(this, Home::class.java)
            startActivity(i)
            finish()
        } else {
            // user is not signed in --> show login/sign in screen
            val i = Intent(this, Register::class.java)
            startActivity(i)
            finish()
        }
    }
}