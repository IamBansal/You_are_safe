package com.example.safetyapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class ContactAdapter(private var context: Context, private var list: ArrayList<Item>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.tvNameItem)
        val relation: TextView = itemView.findViewById(R.id.tvRelationItem)
        val number: TextView = itemView.findViewById(R.id.tvNumberItem)
        val card: CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseAuth = FirebaseAuth.getInstance()

        val contact = list[position]
        holder.contactName.text = contact.name
        holder.relation.text = contact.relation
        holder.number.text = contact.number
        holder.card.setOnLongClickListener { it ->
            val alert = AlertDialog.Builder(context)
            alert.setMessage("You want to remove the contact from emergency contacts?")
            alert.setTitle("Remove Contact!!")
                .setPositiveButton("Yes, Delete!!") { _, _ ->
                    //delete the contact.
                    FirebaseDatabase.getInstance().reference.child("UsersSafety")
                        .child(firebaseAuth.currentUser!!.uid).child(contact.number!!).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(context, "Contact Removed.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}