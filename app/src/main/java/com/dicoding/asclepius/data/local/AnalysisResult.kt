package com.dicoding.asclepius.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "analysis_result")
data class AnalysisResult(

    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @field:ColumnInfo(name = "photoURI")
    val photoURI: String,

    @field:ColumnInfo(name = "predictionResult")
    val predictionResult: String,

    @field:ColumnInfo(name = "confidenceScore")
    val confidenceScore: Int,

    @field:ColumnInfo("createdAt")
    val createdAt: String,

    @field:ColumnInfo("inferenceTime")
    val inferenceTime: Long,

    @field:ColumnInfo("error")
    val error: String? = null

): Parcelable