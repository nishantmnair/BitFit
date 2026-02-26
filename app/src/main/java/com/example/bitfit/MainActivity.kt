package com.example.bitfit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val logs = mutableListOf<FoodEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logRv = findViewById<RecyclerView>(R.id.food_rv)
        val addLogBtn = findViewById<Button>(R.id.add_food_btn)
        val clearDbBtn = findViewById<Button>(R.id.clear_db_btn)
        val avgSleepTv = findViewById<TextView>(R.id.avg_sleep_tv)
        val avgFeelingTv = findViewById<TextView>(R.id.avg_feeling_tv)

        val adapter = FoodAdapter(logs)
        logRv.adapter = adapter
        logRv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            AppDatabase.getInstance(this@MainActivity).foodDao().getAll().collect { databaseList ->
                logs.clear()
                logs.addAll(databaseList)
                adapter.notifyDataSetChanged()
                
                updateAverages(databaseList, avgSleepTv, avgFeelingTv)
            }
        }

        addLogBtn.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        clearDbBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext).foodDao().deleteAll()
            }
        }
    }

    private fun updateAverages(list: List<FoodEntity>, sleepTv: TextView, feelingTv: TextView) {
        if (list.isEmpty()) {
            sleepTv.text = "Average hours of sleep: 0.0 hours"
            feelingTv.text = "Average feeling: 0 / 10"
            return
        }

        val totalSleep = list.sumOf { it.calories?.toDoubleOrNull() ?: 0.0 }
        val avgSleep = (totalSleep / list.size) / 10.0
        
        val totalFeeling = list.sumOf { it.feeling ?: 0 }
        val avgFeeling = totalFeeling.toDouble() / list.size

        sleepTv.text = String.format("Average hours of sleep: %.1f hours", avgSleep)
        feelingTv.text = String.format("Average feeling: %.1f / 10", avgFeeling)
    }
}
