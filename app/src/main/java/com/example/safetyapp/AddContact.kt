package com.example.safetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class AddContact : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var name: EditText
    private lateinit var relation: EditText
    private lateinit var number: EditText
    private lateinit var addContact: Button

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

        addContact.setOnClickListener {
            addContactNumber()
        }

    }

    private fun addContactNumber() {
        Toast.makeText(this, "Number added successfully.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }
}