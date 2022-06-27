package com.example.safetyapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private var signUp : TextView? = null
    private var signIn : Button? = null
    private var email : EditText? = null
    private var password : EditText? = null
    private var firebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signUp = findViewById(R.id.tvNewUser)
        signIn = findViewById(R.id.btnSignIn)
        email = findViewById(R.id.etEmailLogin)
        password = findViewById(R.id.etPassLogin)
        firebaseAuth = FirebaseAuth.getInstance()

        signIn?.setOnClickListener {
            signInUser()
        }

        signUp?.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

    }

    private fun signInUser() {

        val emailText = email?.text.toString().trim()
        val passText = email?.text.toString().trim()

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText)) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Login failed!!")
                .setMessage("Fill all credentials first.")
                .setPositiveButton("Okay"){_,_-> }
                .create()
                .show()
        } else {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Logging in..")
            progressBar.show()

            firebaseAuth?.signInWithEmailAndPassword(emailText, passText)?.addOnCompleteListener { task ->
                progressBar.dismiss()
                if(task.isSuccessful) {
                    Toast.makeText(this, "Logged in successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}