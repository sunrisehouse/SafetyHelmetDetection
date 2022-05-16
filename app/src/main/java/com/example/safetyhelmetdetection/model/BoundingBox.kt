package com.example.safetyhelmetdetection.model

import android.graphics.Rect

data class BoundingBox(
    val label: String,
    val rect: Rect,
)
