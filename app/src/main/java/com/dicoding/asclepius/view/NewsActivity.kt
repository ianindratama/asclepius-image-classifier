package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.viewmodel.NewsViewModel
import com.dicoding.asclepius.viewmodel.NewsViewModelFactory

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    private lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.setHasFixedSize(true)

        setupViewModel()

        newsViewModel.news.observe(this){ articlesItems ->
            if (!articlesItems.isNullOrEmpty()){

                val news = articlesItems.filter { it?.title != "[Removed]" }

                val adapter = NewsAdapter(news)

                adapter.setOnItemClickCallback(object: NewsAdapter.OnItemClickCallback{
                    override fun onItemClicked(newsUrl: String?) {
                        if (newsUrl != null){
                            val web = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(newsUrl)
                            )
                            startActivity(web)
                        }
                    }
                })

                binding.rvNews.adapter = adapter
                binding.circularProgressBar.visibility = View.GONE
            }
        }

        newsViewModel.errorStatus.observe(this){
            if (it != null){
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                binding.circularProgressBar.visibility = View.GONE
            }
        }


    }

    private fun setupViewModel(){

        val factory = NewsViewModelFactory.getInstance()

        newsViewModel = ViewModelProvider(
            this,
            factory
        )[NewsViewModel::class.java]

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}