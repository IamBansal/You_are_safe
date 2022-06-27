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
import java.util.regex.Matcher
import java.util.regex.Pattern

class Signup : AppCompatActivity() {

    private var signIn: TextView? = null
    private var signUp: Button? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signUp = findViewById(R.id.btnSignUp)
        signIn = findViewById(R.id.tvAlreadyUser)
        email = findViewById(R.id.etEmailSignup)
        password = findViewById(R.id.etPassSignup)
        confirmPassword = findViewById(R.id.etConfirmPassSignup)
        firebaseAuth = FirebaseAuth.getInstance()

        signUp?.setOnClickListener {
            signUpUser()
        }

        signIn?.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

    }

    private fun signUpUser() {
        val emailText = email?.text.toString().trim()
        val passwordText = password?.text.toString().trim()
        val confirmPasswordText = confirmPassword?.text.toString().trim()

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Signup failed!!")
                .setMessage("Fill all credentials first.")
                .setPositiveButton("Okay") { _, _ -> }
                .create()
                .show()
        } else if (passwordText != confirmPasswordText) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Signup failed!!")
                .setMessage("Password didn't matched.")
                .setPositiveButton("Okay") { _, _ -> }
                .create()
                .show()
        } else {
            if (isValidPassword(passwordText)) {
                val progressBar = ProgressDialog(this)
                progressBar.setMessage("Signing you up..")
                progressBar.show()

                firebaseAuth?.createUserWithEmailAndPassword(emailText, passwordText)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Signed up successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, Login::class.java))
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Weak password...\nStrong password must contain lowercase, uppercase alphabet," +
                            " a digit, a special character with no spaces.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"

        /*

        REGEX condition explanation....

        (?=.*[0-9])  This is for that it should have at least a digit.
        (?=.*[a-z])  This is for that it should have at least a lowercase alphabet.
        (?=.*[A-Z])  This is for that it should have at least a uppercase alphabet.
        (?=.*[$specialCharacters])  This is for that it should have at least a special character which are defined above..
        (?=\S+$).{8,20}  This is for that it should have at least 8 and at most 20 characters without any space..

         */

        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
        pattern = Pattern.compile(passwordRegex)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Login::class.java))
    }

}