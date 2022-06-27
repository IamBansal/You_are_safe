package com.example.safetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Signup : AppCompatActivity() {

    private var signIn : TextView? = null
    private var signUp : Button? = null
    private var email : EditText? = null
    private var password : EditText? = null
    private var confirmPassword : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signUp = findViewById(R.id.btnSignUp)
        signIn = findViewById(R.id.tvAlreadyUser)
        email = findViewById(R.id.etEmailSignup)
        password = findViewById(R.id.etPassSignup)
        confirmPassword = findViewById(R.id.etConfirmPassSignup)

        signUp?.setOnClickListener {
            signUpUser()
        }

        signIn?.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

    }

    private fun signUpUser() {
        startActivity(Intent(this, Login::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Login::class.java))
    }

}