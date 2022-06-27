package com.example.safetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        addContact = findViewById(R.id.btnAdd)
        firebaseAuth = FirebaseAuth.getInstance()

        addContact.setOnClickListener {
            addContactNumber()
        }

    }

    private fun addContactNumber() {

        val nameText = name.text.toString().trim()
        val relationText = relation.text.toString().trim()
        val numberText = number.text.toString().trim()

        val map = HashMap<String, Any>()
        map["name"] = nameText
        map["relation"] = relationText
        map["number"] = numberText

        if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(relationText) || TextUtils.isEmpty(
                numberText) || numberText.length != 10) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Adding new contact failed!!")
                .setMessage("Fill all credentials first or check your number again.")
                .setPositiveButton("Okay") { _, _ -> }
                .create()
                .show()
        } else {
            FirebaseDatabase.getInstance().reference.child("UsersSafety")
                .child(firebaseAuth.currentUser!!.uid).child(numberText).updateChildren(map)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Number added successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}