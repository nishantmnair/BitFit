package com.example.bitfit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private var photoPath: String? = null
    private lateinit var photoPreview: ImageView

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            photoPreview.setImageBitmap(imageBitmap)
            photoPreview.visibility = View.VISIBLE
            saveBitmapToFile(imageBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val sleepSeekBar = findViewById<SeekBar>(R.id.sleep_seekbar)
        val sleepValueTv = findViewById<TextView>(R.id.sleep_value_tv)
        val feelingSeekBar = findViewById<SeekBar>(R.id.feeling_seekbar)
        val feelingValueTv = findViewById<TextView>(R.id.feeling_value_tv)
        val notesInput = findViewById<EditText>(R.id.notes_input)
        val takePhotoBtn = findViewById<Button>(R.id.take_photo_btn)
        val recordBtn = findViewById<Button>(R.id.record_btn)
        photoPreview = findViewById(R.id.photo_preview)

        // Initial values
        updateSleepText(sleepSeekBar.progress, sleepValueTv)
        updateFeelingText(feelingSeekBar.progress, feelingValueTv)

        sleepSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSleepText(progress, sleepValueTv)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        feelingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateFeelingText(progress, feelingValueTv)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        takePhotoBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(takePictureIntent)
        }

        recordBtn.setOnClickListener {
            // Sleep value is stored as (hours * 10) to maintain consistency with the existing logic
            // where 0.5 hours = 5, 1 hour = 10, etc.
            val sleepValue = (sleepSeekBar.progress * 5).toString() 
            val feelingValue = feelingSeekBar.progress
            val notes = notesInput.text.toString()
            val dateStr = SimpleDateFormat("MMM d yyyy", Locale.US).format(Date()).uppercase()

            val log = FoodEntity(
                name = "Log",
                calories = sleepValue,
                feeling = feelingValue,
                notes = notes,
                photoPath = photoPath,
                date = dateStr
            )

            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext).foodDao().insert(log)
                finish()
            }
        }
    }

    private fun updateSleepText(progress: Int, textView: TextView) {
        val hours = progress / 2.0
        textView.text = String.format("%.1f hours", hours)
    }

    private fun updateFeelingText(progress: Int, textView: TextView) {
        textView.text = "$progress / 10"
    }

    private fun saveBitmapToFile(bitmap: Bitmap) {
        val filename = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(externalCacheDir, filename)
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            photoPath = file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
