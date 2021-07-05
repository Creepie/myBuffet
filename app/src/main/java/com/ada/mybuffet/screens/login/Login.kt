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
import com.ada.mybuffet.databinding.ActivityLoginBinding
import com.ada.mybuffet.screens.home.Home
import com.ada.mybuffet.utils.EspressoIdlingResource
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginBTRegisterMail.setOnClickListener {
            // go back to register
            val i = Intent(this, Register::class.java)
            startActivity(i)
            finish()
        }

        val loginProgressButton = binding.loginBTLogIn
        loginProgressButton.setOnClickListener {
            performLogin(loginProgressButton)
        }

        binding.loginTvResetPassword.setOnClickListener {
            val i = Intent(this, ResetPassword::class.java)
            startActivity(i)
        }
    }

    private fun performLogin(loginProgressButton: CircularProgressButton) {
        val email = binding.loginEtEmail.text.toString()
        val password = binding.loginEtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.register_fields_not_filled), Toast.LENGTH_SHORT).show()
            return
        }

        // increment idling resource counter for UI testing
        EspressoIdlingResource.increment()

        // start button animation
        loginProgressButton.startAnimation()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val btnFillColor = ContextCompat.getColor(applicationContext, R.color.accent_darker)
                loginProgressButton.doneLoadingAnimation(btnFillColor, BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))

                // add a short delay in order to briefly show the finished animation
                // and then launch Home
                Handler(Looper.getMainLooper()).postDelayed({
                    // decrement idling resource counter for UI test
                    EspressoIdlingResource.decrement()

                    // perform intent
                    val i = Intent(this, Home::class.java)
                    startActivity(i)
                    finish()
                }, 500)
            }
            .addOnFailureListener {
                // decrement idling resource counter for UI test
                EspressoIdlingResource.decrement()

                // undo animation and reset button
                loginProgressButton.revertAnimation()

                // show toast message
                Toast.makeText(applicationContext, applicationContext.getString(R.string.login_failure_message), Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }
}