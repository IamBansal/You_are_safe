package com.example.safetyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var addContact : FloatingActionButton
    private lateinit var recyclerView : RecyclerView
    private lateinit var stop : Button
    private lateinit var help : ImageView
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactList: ArrayList<Item>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbarM)
        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar?.title = "You are safe!!"

        addContact = findViewById(R.id.fabAddContact)
        stop = findViewById(R.id.btnStop)
        help = findViewById(R.id.ivHelp)
        recyclerView = findViewById(R.id.rvContactList)
        contactList = ArrayList()
        contactAdapter = ContactAdapter(this, contactList)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = contactAdapter
        getContacts()


//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Shagun", "Sister", "9886777325"))
//        contactList.add(Item("Sunita", "Mother", "900077325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))
//        contactList.add(Item("Akshat", "Brother", "9871877325"))


        addContact.setOnClickListener {
            startActivity(Intent(this, AddContact::class.java))
        }

        help.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }

        stop.setOnClickListener {
            stopMessages()
        }

    }

    private fun getContacts() {
            FirebaseDatabase.getInstance().reference.child("UsersSafety").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactList.clear()
                    for (dataSnaps in snapshot.children){
                        val item = dataSnaps.getValue(Item::class.java)
                        contactList.add(item!!)
                    }
                    contactAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun stopMessages() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}