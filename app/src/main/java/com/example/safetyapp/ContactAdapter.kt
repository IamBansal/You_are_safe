package com.example.safetyapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class ContactAdapter(private var context: Context, private var list : ArrayList<Item>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val contactName : TextView = itemView.findViewById(R.id.tvNameItem)
        val relation : TextView = itemView.findViewById(R.id.tvRelationItem)
        val number : TextView = itemView.findViewById(R.id.tvNumberItem)
        val card : CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
                }
                .setNegativeButton("No"){_,_->}
                .create()
                .show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}