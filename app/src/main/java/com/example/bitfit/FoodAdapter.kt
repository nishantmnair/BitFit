package com.example.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.BitmapFactory
import java.io.File

class FoodAdapter(private val foods: List<FoodEntity>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_tv)
        val notesTextView: TextView = view.findViewById(R.id.notes_tv)
        val sleepValTextView: TextView = view.findViewById(R.id.sleep_val_tv)
        val feelingValTextView: TextView = view.findViewById(R.id.feeling_val_tv)
        val photoImageView: ImageView = view.findViewById(R.id.item_photo_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = foods[position]
        holder.dateTextView.text = log.date ?: "No Date"
        holder.notesTextView.text = log.notes ?: ""
        
        val hours = (log.calories?.toDoubleOrNull() ?: 0.0) / 10.0
        holder.sleepValTextView.text = "Slept $hours\nhours"
        holder.feelingValTextView.text = "Feeling ${log.feeling}/10"

        if (log.photoPath != null) {
            val imgFile = File(log.photoPath)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.photoImageView.setImageBitmap(myBitmap)
                holder.photoImageView.visibility = View.VISIBLE
            } else {
                holder.photoImageView.visibility = View.GONE
            }
        } else {
            holder.photoImageView.visibility = View.GONE
        }
    }

    override fun getItemCount() = foods.size
}
