package com.ada.mybuffet.screens.login

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.ActivityResetPasswordBinding
import com.ada.mybuffet.screens.home.Home
import com.google.firebase.auth.FirebaseAuth

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

        val resetPasswordButton = binding.resetPasswordBtnReset
        resetPasswordButton.setOnClickListener {
            performPasswordReset(resetPasswordButton)
        }
    }

    private fun performPasswordReset(resetPasswordButton: CircularProgressButton) {
        val email = binding.resetPasswordEtEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_fields_not_filled), Toast.LENGTH_SHORT).show()
            return
        }

        resetPasswordButton.startAnimation()

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                val btnFillColor = ContextCompat.getColor(applicationContext, R.color.accent_darker)
                resetPasswordButton.doneLoadingAnimation(btnFillColor, BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                Toast.makeText(applicationContext, applicationContext.getString(R.string.resetPassword_success_message), Toast.LENGTH_LONG).show()

                // add a short delay in order to briefly show the finished animation
                // and then launch Home
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 500)
            }
            .addOnFailureListener {
                resetPasswordButton.revertAnimation()

                Toast.makeText(applicationContext, applicationContext.getString(R.string.resetPassword_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }
}