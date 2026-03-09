package com.example.bitfit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val foodInput = findViewById<EditText>(R.id.food_input)
        val caloriesInput = findViewById<EditText>(R.id.calories_input)
        val recordBtn = findViewById<Button>(R.id.record_btn)

        recordBtn.setOnClickListener {
            val foodName = foodInput.text.toString()
            val calories = caloriesInput.text.toString()

            val log = FoodEntity(
                name = foodName,
                calories = calories
            )

            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext).foodDao().insert(log)
                finish()
            }
        }
    }
}
