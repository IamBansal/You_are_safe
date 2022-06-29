package com.example.safetyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var addContact: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var stop: Button
    private lateinit var send: Button
    private lateinit var help: ImageView
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactList: ArrayList<Item>
    private lateinit var firebaseAuth: FirebaseAuth
    private var latitude: Any? = null
    private var longitude: Any? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbarM)
        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar?.title = "You are safe!!"

        addContact = findViewById(R.id.fabAddContact)
        stop = findViewById(R.id.btnStop)
        send = findViewById(R.id.btnSend)
        help = findViewById(R.id.ivHelp)
        recyclerView = findViewById(R.id.rvContactList)
        contactList = ArrayList()
        contactAdapter = ContactAdapter(this, contactList)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = contactAdapter
        getContacts()

        addContact.setOnClickListener {
            startActivity(Intent(this, AddContact::class.java))
        }

        help.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }

        stop.setOnClickListener {
            stopMessages()
        }

        send.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("UsersSafety")
                .child(firebaseAuth.currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            sendMessage()
                        } else {
                            val alert = AlertDialog.Builder(this@MainActivity)
                            alert.setTitle("No Emergency Contact Found!!")
                                .setMessage("Add at least one number in the emergency contact for sending the message.")
                                .setPositiveButton("Okay") { _, _ -> }
                                .create()
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(
            sensorListener,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    //To check for shake device.
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            val x = p0!!.values[0]
            val y = p0.values[1]
            val z = p0.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 25) {
                Toast.makeText(
                    applicationContext,
                    "Shake detected..\nSending the emergency message.",
                    Toast.LENGTH_SHORT
                ).show()
                sendMessage()
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        //To detect shake when app running in background.
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )

        //To unregister the sensor when app running in background.
//        sensorManager!!.unregisterListener(sensorListener)

        super.onPause()
    }

    //To get the location
    private fun getCurrentLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {
                //To check Permissions
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }

                //To fetch the location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        longitude = location.longitude
                        latitude = location.latitude
                    }
                }

            } else {
                //To send user to settings to turn on the location.
                Toast.makeText(this, "Turn on location.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission()
        }
    }

    //To check if location is enabled or not.
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //To request the permissions.
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    //To show that permissions are checked.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //To check if permissions are granted or not.
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    //To send a message on stopping the messages.
    private fun stopMessages() {
        val obj = SmsManager.getDefault()
        for (i in contactList) {
            obj.sendTextMessage(
                i.number,
                null,
                "I am Safe!\nThanks for your concern :)",
                null,
                null
            )
        }
        Toast.makeText(applicationContext, "Safe Message sent.", Toast.LENGTH_SHORT)
            .show()
        stop.visibility = View.GONE
    }

    //Function to send message to the contacts.
    private fun sendMessage() {
//        val url = "http://maps.google.com/maps?q=28.857556%2C77.085029"
        val url = "http://maps.google.com/maps?q=$latitude,$longitude"
        val obj = SmsManager.getDefault()
        for (i in contactList) {
            obj.sendTextMessage(
                i.number,
                null,
                "Hey!\nText from 'You are safe!!' app.\nI AM IN DANGER.\nMy location is $url",
                null,
                null
            )
        }
        Toast.makeText(applicationContext, "Emergency Message sent.", Toast.LENGTH_SHORT).show()
        stop.visibility = View.VISIBLE
    }

    //To get the contacts list
    private fun getContacts() {
        FirebaseDatabase.getInstance().reference.child("UsersSafety")
            .child(firebaseAuth.currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactList.clear()
                    for (dataSnaps in snapshot.children) {
                        val item = dataSnaps.getValue(Item::class.java)
                        contactList.add(item!!)
                    }
                    contactAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                logout()
                return true
            }
            R.id.deleteAcc -> {
                deleteAccount()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Logout Requested!!")
            .setMessage("You sure you want to logout?")
            .setPositiveButton("Yes, Logout!!") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this,
                        Login::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            .setNegativeButton("No") { _, _ -> }
            .create()
            .show()
    }

    private fun deleteAccount() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Delete Account Requested!!")
            .setMessage("You sure you want to delete your account?\nYou will not be able to recover your data once you delete account.")
            .setPositiveButton("Yes, Delete!!") { _, _ ->
                FirebaseAuth.getInstance().currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this,
                                    Login::class.java
                                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            .setNegativeButton("No") { _, _ -> }
            .create()
            .show()
    }

    //For long press action.
    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.d("test", "Long Press!!")
            sendMessage()
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event?.startTracking()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

}