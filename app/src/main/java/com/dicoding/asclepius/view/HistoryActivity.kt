package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.setHasFixedSize(true)

        setupHistoryAdapter()
        setupViewModel()

        historyViewModel.getAnalysisResult().observe(this){
            binding.circularProgressBar.visibility = View.GONE

            if (it.isEmpty()){
                binding.rvHistory.visibility = View.GONE
                binding.tvNoResult.visibility = View.VISIBLE
                binding.btnNoResult.visibility = View.VISIBLE
            } else {
                binding.rvHistory.visibility = View.VISIBLE
                binding.tvNoResult.visibility = View.GONE
                binding.btnNoResult.visibility = View.GONE

                historyAdapter.submitList(it)
            }
        }

        binding.btnNoResult.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setupHistoryAdapter(){
        historyAdapter = HistoryAdapter()

        historyAdapter.setOnItemClickCallback(object: HistoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: AnalysisResult) {
                val intent = Intent(this@HistoryActivity, ResultActivity::class.java)
                intent.putExtra("ANALYSIS_DATA", data)
                startActivity(intent)
            }

        })

        binding.rvHistory.adapter = historyAdapter
    }

    private fun setupViewModel(){

        val factory = HistoryViewModelFactory.getInstance(this)

        historyViewModel = ViewModelProvider(
            this,
            factory
        )[HistoryViewModel::class.java]

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}