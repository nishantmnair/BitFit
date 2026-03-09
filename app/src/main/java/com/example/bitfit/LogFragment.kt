package com.example.bitfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitfit.databinding.FragmentLogBinding
import kotlinx.coroutines.launch

class LogFragment : Fragment() {
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val logs = mutableListOf<FoodEntity>()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FoodAdapter(logs)
        binding.foodRv.adapter = adapter
        binding.foodRv.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).foodDao().getAll().collect { databaseList ->
                logs.clear()
                logs.addAll(databaseList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}