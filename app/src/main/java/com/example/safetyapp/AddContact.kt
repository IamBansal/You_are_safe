package com.example.safetyapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddContact : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var name: EditText
    private lateinit var relation: EditText
    private lateinit var number: EditText
    private lateinit var otpEnter: EditText
    private lateinit var addContact: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        toolbar = findViewById(R.id.toolbarAdd)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Emergency Contact"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        name = findViewById(R.id.etName)
        relation = findViewById(R.id.etRelation)
        number = findViewById(R.id.etNumber)
        otpEnter = findViewById(R.id.etOtp)
        addContact = findViewById(R.id.btnAdd)
        firebaseAuth = FirebaseAuth.getInstance()

        addContact.setOnClickListener {
            addContactNumber()
        }

        addContact.tag = "send"
        addContact.text = "Send OTP"

    };

    private fun addContactNumber() {

        val nameText = name.text.toString().trim()
        val relationText = relation.text.toString().trim()
        val numberText = number.text.toString().trim()
        if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(relationText) || TextUtils.isEmpty(
                numberText
            ) || numberText.length != 10
        ) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Adding new contact failed!!")
                .setMessage("Fill all credentials first or check your number again.")
                .setPositiveButton("Okay") { _, _ -> }
                .create()
                .show()
        } else {
            val otp = (1000..9999).random().toString()
            Toast.makeText(this, "OTP is : $otp", Toast.LENGTH_SHORT).show()
            val obj = SmsManager.getDefault()
            obj.sendTextMessage(
                numberText,
                null,
                "Hey!\nI have added your number as emergency contact in 'You are Safe!!' app.\nPlease verify the number by otp.\nOTP is $otp .\nOTP will expire in 20 seconds.",
                null,
                null
            )

            Handler().postDelayed({
                if (otpVerified(otp)) {
                    val map = HashMap<String, Any>()
                    map["name"] = nameText
                    map["relation"] = relationText
                    map["number"] = numberText

                    FirebaseDatabase.getInstance().reference.child("UsersSafety")
                        .child(firebaseAuth.currentUser!!.uid).child(numberText).updateChildren(map)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Number added successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                addContact.tag = "add"
                                addContact.text = "Add contact"
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                } else {
                    Toast.makeText(this, "OTP not verified.", Toast.LENGTH_SHORT).show()
                }
            }, 10000)
        }
    }

    //For verifying otp.
    private fun otpVerified(otp: String): Boolean {
        val otpText = otpEnter.text.toString()
        return if (otpText == otp) {
            true
        } else {
            Toast.makeText(this, "OTP not verified.", Toast.LENGTH_SHORT).show()
            false
        }
    }

}