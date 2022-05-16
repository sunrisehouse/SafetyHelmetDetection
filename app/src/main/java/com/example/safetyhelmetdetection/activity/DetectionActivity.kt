package com.example.safetyhelmetdetection.activity

import android.graphics.Rect
import android.os.Bundle
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import com.example.safetyhelmetdetection.R
import com.example.safetyhelmetdetection.activity.abstraction.CameraXActivity
import com.example.safetyhelmetdetection.model.BoundingBox
import com.example.safetyhelmetdetection.model.DetectionObject
import com.example.safetyhelmetdetection.view.BoundingBoxDisplayView

class DetectionActivity : CameraXActivity<DetectionActivity.AnalysisResult>() {
    private lateinit var boundingBoxDisplayView: BoundingBoxDisplayView
    class AnalysisResult(val objects: List<DetectionObject>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection)
        boundingBoxDisplayView = findViewById(R.id.boundingBoxDisplayView)
    }

    override fun buildPreview(): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(findViewById<PreviewView>(R.id.cameraPreviewView).surfaceProvider)
            }
    }

    override fun analyzeImage(imageProxy: ImageProxy, rotationDegrees: Int): AnalysisResult {
        val result = listOf(
            DetectionObject(0, 0.9f, Rect(10, 10, 200, 200)),
            DetectionObject(1, 0.9f, Rect(400, 400, 700, 700)),
        )
        return AnalysisResult(result)
    }

    override fun applyToUiAnalyzeImageResult(result: AnalysisResult) {
        val labels = listOf("class 1", "class 2")
        boundingBoxDisplayView.boundingBoxes = result.objects.map { o ->
            BoundingBox(String.format("%s %.2f", labels[o.classIndex], o.score), o.rect)
        }
        boundingBoxDisplayView.invalidate()
    }
}