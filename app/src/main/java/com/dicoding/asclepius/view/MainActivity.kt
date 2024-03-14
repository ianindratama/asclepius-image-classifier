package com.dicoding.asclepius.view

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.timeStamp
import com.dicoding.asclepius.uriToFile
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.MainViewModelFactory
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null
    private var analysisResult: AnalysisResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupImageClassifierHelper()

        mainViewModel.newRecordID.observe(this@MainActivity){
            if (it != null){
                analysisResult?.id = it
                moveToResult(analysisResult!!)
            }
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            if (currentImageUri == null){
                Toast.makeText(this@MainActivity, "Please select an image to be analyze first!", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressIndicator.visibility = View.VISIBLE
                analyzeImage()
                binding.progressIndicator.visibility = View.GONE
            }
        }

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.history -> {
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                    true
                }
                R.id.news -> {
                    startActivity(Intent(this@MainActivity, NewsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupViewModel(){
        val factory = MainViewModelFactory.getInstance(this)

        mainViewModel = ViewModelProvider(
            this,
            factory
        )[MainViewModel::class.java]
    }

    private fun setupImageClassifierHelper(){
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object: ImageClassifierHelper.ClassifierListener{
                override fun onError(error: String) {
                    analysisResult = AnalysisResult(
                        photoURI = currentImageUri?.toString() ?: "",
                        predictionResult = "Undefined",
                        confidenceScore = 0,
                        createdAt = timeStamp,
                        inferenceTime = 0L,
                        error = error
                    )
                    mainViewModel.insertAnalysisResult(analysisResult!!)
                }

                override fun onResults(result: List<Classifications>?, inferenceTime: Long) {
                    val firstItem = result?.first()?.categories?.first()
                    val lastItem = result?.first()?.categories?.last()

                    analysisResult = if(firstItem?.score!! >= lastItem?.score!!) {
                        AnalysisResult(
                            photoURI = currentImageUri?.toString() ?: "",
                            predictionResult = firstItem.label,
                            confidenceScore = (firstItem.score * 100).toInt(),
                            createdAt = timeStamp,
                            inferenceTime = inferenceTime
                        )
                    } else {
                        AnalysisResult(
                            photoURI = currentImageUri?.toString() ?: "",
                            predictionResult = lastItem.label,
                            confidenceScore = (lastItem.score * 100).toInt(),
                            createdAt = timeStamp,
                            inferenceTime = inferenceTime
                        )
                    }

                    mainViewModel.insertAnalysisResult(analysisResult!!)
                }

            }
        )
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        binding.previewImageView.setImageURI(currentImageUri)
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        runBlocking {
            currentImageUri?.let { imageClassifierHelper.classifyStaticImage(it) }
        }
    }

    private fun moveToResult(analysisResult: AnalysisResult) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("ANALYSIS_DATA", analysisResult)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            val destinationFile =  uriToFile(selectedImg, this@MainActivity)

            val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
                .setTitle("Image captured!")
                .setMessage("Do you want to edit the captured photo?")
                .setPositiveButton("Yes"){ dialogView, _ ->
                    val intentToUCropActivity = UCrop.of(selectedImg, destinationFile.toUri())
                        .getIntent(this@MainActivity)
                    launcherUCropActivity.launch(intentToUCropActivity)
                    dialogView.dismiss()
                }
                .setNegativeButton("No"){ dialogView, _ ->
                    currentImageUri = destinationFile.toUri()
                    showImage()

                    dialogView.cancel()
                }
                .setOnDismissListener { dialogView ->
                    dialogView.dismiss()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            showToast("Unable to load image from Gallery")
        }
    }

    private val launcherUCropActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val editedImageUri = UCrop.getOutput(result.data!!)

            if (editedImageUri != null){
                currentImageUri = editedImageUri
                showImage()
            } else {
                showToast("An error has occurred during the process. Please try again later")
            }
        } else {
            showToast("An error has occurred during the process. Please try again later")
        }

    }

}