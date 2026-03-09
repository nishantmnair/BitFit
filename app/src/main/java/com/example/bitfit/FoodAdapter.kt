package com.example.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(private val foods: List<FoodEntity>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name_tv)
        val caloriesTextView: TextView = view.findViewById(R.id.calories_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foods[position]
        holder.nameTextView.text = food.name
        holder.caloriesTextView.text = food.calories
    }

    override fun getItemCount() = foods.size
}
