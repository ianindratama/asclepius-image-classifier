package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(
    val context: Context,
    private val modelName: String = "cancer_classification.tflite",
    val classifierListener: ClassifierListener?
) {

    private var imageClassifier: ImageClassifier? = null

    interface ClassifierListener{
        fun onError(error: String)
        fun onResults(
            result: List<Classifications>?,
            inferenceTime: Long
        )
    }

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
        val baseOptionsBuilder = BaseOptions.builder()

        if (CompatibilityList().isDelegateSupportedOnThisDevice){
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            baseOptionsBuilder.useNnapi()
        } else {
            // Menggunakan CPU
            baseOptionsBuilder.setNumThreads(4)
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        }catch (e: IllegalStateException){
            classifierListener?.onError("Image classifier failed to initialize. See error logs for details")
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(CastOp(DataType.FLOAT32))
                .build()

            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

            var inferenceTime = SystemClock.uptimeMillis()
            val results = imageClassifier?.classify(tensorImage)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime
            classifierListener?.onResults(
                results,
                inferenceTime
            )
        }

    }


    companion object {
        private const val TAG = "ImageClassifierHelper"
    }

}