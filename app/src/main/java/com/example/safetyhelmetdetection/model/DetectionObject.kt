package com.example.safetyhelmetdetection.model

import android.graphics.Rect

data class DetectionObject(
    val classIndex: Int,
    val score: Float,
    val rect: Rect,
)
