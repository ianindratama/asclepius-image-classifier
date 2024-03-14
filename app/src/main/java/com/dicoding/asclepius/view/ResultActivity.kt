package com.dicoding.asclepius.view

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.core.text.bold
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.parseAnalysisTimeStamp
import com.dicoding.asclepius.viewmodel.ResultViewModel
import com.dicoding.asclepius.viewmodel.ResultViewModelFactory
import kotlinx.coroutines.runBlocking


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private lateinit var resultViewModel: ResultViewModel

    private var analysisResult: AnalysisResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        setupViewModel()
        setupAnalysisResult()

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    if (this@ResultActivity::resultViewModel.isInitialized && analysisResult != null){
                        val alertDialogBuilder = AlertDialog.Builder(this@ResultActivity)
                            .setTitle("Action Confirmation")
                            .setMessage("Are you sure to delete this analysis?")
                            .setPositiveButton("Yes"){ dialogView, _ ->
                                dialogView.dismiss()

                                runBlocking {
                                    resultViewModel.deleteAnalysisResult(analysisResult!!)
                                }

                                onSupportNavigateUp()
                            }
                            .setNegativeButton("No"){ dialogView, _ ->
                                dialogView.cancel()
                            }
                            .setOnDismissListener { dialogView ->
                                dialogView.dismiss()
                            }

                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }
                    true
                }
                else -> false
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onSupportNavigateUp()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.result_app_bar, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupViewModel(){
        val factory = ResultViewModelFactory.getInstance(this)

        resultViewModel = ViewModelProvider(
            this,
            factory
        )[ResultViewModel::class.java]
    }

    private fun setupAnalysisResult(){
        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        analysisResult = getParcelableExtra(intent, "ANALYSIS_DATA", AnalysisResult::class.java)

        if (analysisResult?.error != null){
            binding.tvAnalysisResult.text = analysisResult?.predictionResult
            binding.resultImage.setImageURI(Uri.parse(analysisResult?.photoURI))
            binding.resultText.text = analysisResult?.error
        } else {
            binding.tvAnalysisResult.text = analysisResult?.predictionResult
            binding.tvAnalysisResult.setTextColor(
                Color.parseColor(
                    analysisResult!!.predictionResult.let {
                        if (it.lowercase() == "cancer"){
                            "#EE4266"
                        } else {
                            "#337357"
                        }
                    }
                )
            )

            binding.resultImage.setImageURI(Uri.parse(analysisResult?.photoURI))

            binding.resultText.text = SpannableStringBuilder()
                .append("We have classified the image to be ")
                .bold { append(analysisResult?.predictionResult) }
                .append(" with confidence score of ")
                .bold { append(analysisResult?.confidenceScore.toString() + "%") }

            binding.tvInferenceTime.text = SpannableStringBuilder()
                .append("Inference time: ")
                .bold { append(analysisResult?.inferenceTime.toString() + "ms") }

            binding.tvAnalyseOn.text = SpannableStringBuilder()
                .append("Analyze on ")
                .bold { append(analysisResult?.createdAt?.let { parseAnalysisTimeStamp(it) }) }
        }


    }

}