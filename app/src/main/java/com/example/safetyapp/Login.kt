package com.example.safetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Login : AppCompatActivity() {

    private var signUp : TextView? = null
    private var signIn : Button? = null
    private var email : EditText? = null
    private var password : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signUp = findViewById(R.id.tvNewUser)
        signIn = findViewById(R.id.btnSignIn)
        email = findViewById(R.id.etEmailLogin)
        password = findViewById(R.id.etPassLogin)

        signIn?.setOnClickListener {
            signInUser()
        }

        signUp?.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

    }

    private fun signInUser() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}