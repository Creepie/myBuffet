package com.ada.mybuffet

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ada.mybuffet.screens.login.Login
import com.ada.mybuffet.screens.login.Register

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, Register::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}