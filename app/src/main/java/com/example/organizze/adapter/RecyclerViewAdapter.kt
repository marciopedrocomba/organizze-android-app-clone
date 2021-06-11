package com.example.organizze.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.organizze.R
import com.example.organizze.model.Movement

class RecyclerViewAdapter(
    val context: Context,
    private val movementsList: List<Movement>
): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.textViewDescription)
        val value: TextView = view.findViewById(R.id.textViewValue)
        val category: TextView = view.findViewById(R.id.textViewCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.user_movement_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val movement = movementsList[position]

        holder.description.text = movement.description

        if(movement.type.toString().startsWith("e")) {
            holder.value.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDespesa))
            holder.value.text = String.format("-%.2f", movement.value)
        }else {
            holder.value.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryReceita))
            holder.value.text = String.format("%.2f", movement.value)
        }

        holder.category.text = movement.category

    }

    override fun getItemCount() = movementsList.size


}