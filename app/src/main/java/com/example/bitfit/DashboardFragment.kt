package com.example.bitfit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bitfit.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).foodDao().getAll().collect { databaseList ->
                updateDashboard(databaseList)
                setupChart(binding.chart, databaseList)
            }
        }

        binding.clearDataBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).foodDao().deleteAll()
            }
        }
    }

    private fun updateDashboard(list: List<FoodEntity>) {
        if (list.isEmpty()) {
            binding.avgCaloriesTv.text = "Average Calories: 0"
            binding.minMaxTv.text = "Min: 0 / Max: 0"
            return
        }

        val caloriesList = list.mapNotNull { it.calories?.toDoubleOrNull() }
        
        if (caloriesList.isEmpty()) {
            binding.avgCaloriesTv.text = "Average Calories: 0"
            binding.minMaxTv.text = "Min: 0 / Max: 0"
            return
        }

        val avgCalories = caloriesList.average()
        val minCalories = caloriesList.minOrNull() ?: 0.0
        val maxCalories = caloriesList.maxOrNull() ?: 0.0

        binding.avgCaloriesTv.text = String.format("Average Calories: %.2f", avgCalories)
        binding.minMaxTv.text = String.format("Min: %.1f / Max: %.1f", minCalories, maxCalories)
    }

    private fun setupChart(chart: LineChart, list: List<FoodEntity>) {
        val caloriesEntries = mutableListOf<Entry>()

        list.forEachIndexed { index, entity ->
            val caloriesVal = entity.calories?.toFloatOrNull() ?: 0f
            caloriesEntries.add(Entry(index.toFloat(), caloriesVal))
        }

        val caloriesDataSet = LineDataSet(caloriesEntries, "Calories").apply {
            color = Color.parseColor("#4CAF50")
            setCircleColor(Color.parseColor("#4CAF50"))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            valueTextColor = Color.parseColor("#4CAF50")
        }

        val lineData = LineData(caloriesDataSet)
        chart.data = lineData
        chart.description.isEnabled = false
        chart.xAxis.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
