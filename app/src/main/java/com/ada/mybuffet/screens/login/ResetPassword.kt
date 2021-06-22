package com.ada.mybuffet.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.ActivityLoginBinding
import com.ada.mybuffet.databinding.ActivityResetPasswordBinding
import com.ada.mybuffet.screens.home.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPassword : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.resetPasswordBtnClose.setOnClickListener {
            finish()
        }

        binding.resetPasswordBtnReset.setOnClickListener {
            performPasswordReset()
        }
    }

    private fun performPasswordReset() {
        val email = binding.resetPasswordEtEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_fields_not_filled), Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.resetPassword_success_message), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.resetPassword_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }
}