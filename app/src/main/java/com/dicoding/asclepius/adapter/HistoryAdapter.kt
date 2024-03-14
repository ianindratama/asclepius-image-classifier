package com.dicoding.asclepius.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.AnalysisResult

class HistoryAdapter : ListAdapter<AnalysisResult, HistoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.history_item_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val analysisResult = getItem(holder.adapterPosition)

        holder.imgPhoto.setImageURI(Uri.parse(analysisResult.photoURI))

        holder.tvClassification.text = analysisResult.predictionResult
        holder.tvClassification.setTextColor(
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

        holder.tvConfidence.text = SpannableStringBuilder()
            .append("Confidence score: ")
            .bold {
                append(
                    if (analysisResult.error != null){
                        "Null"
                    } else {
                        analysisResult.confidenceScore.toString() + "%"
                    }
                )
            }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(getItem(holder.adapterPosition))
        }

    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvClassification: TextView = itemView.findViewById(R.id.tv_item_classification)
        var tvConfidence: TextView = itemView.findViewById(R.id.tv_item_confidenceScore)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: AnalysisResult)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<AnalysisResult> =
            object : DiffUtil.ItemCallback<AnalysisResult>() {
                override fun areItemsTheSame(oldAnalysisResult: AnalysisResult, newAnalysisResult: AnalysisResult): Boolean {
                    return oldAnalysisResult.id == newAnalysisResult.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldAnalysisResult: AnalysisResult, newAnalysisResult: AnalysisResult): Boolean {
                    return oldAnalysisResult == newAnalysisResult
                }
            }
    }

}