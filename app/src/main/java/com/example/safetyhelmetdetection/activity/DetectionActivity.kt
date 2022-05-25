package com.example.safetyhelmetdetection.activity

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import com.example.safetyhelmetdetection.R
import com.example.safetyhelmetdetection.SafetyHelmetDetectionApplication
import com.example.safetyhelmetdetection.activity.abstraction.CameraXActivity
import com.example.safetyhelmetdetection.analyzer.ObjectInImageAnalyzer
import com.example.safetyhelmetdetection.model.BoundingBox
import com.example.safetyhelmetdetection.model.DetectionObject
import com.example.safetyhelmetdetection.processor.PrePostProcessor
import com.example.safetyhelmetdetection.view.BoundingBoxDisplayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class DetectionActivity : CameraXActivity<DetectionActivity.AnalysisResult>() {
    private lateinit var boundingBoxDisplayView: BoundingBoxDisplayView
    private val objectInImageAnalyzer = ObjectInImageAnalyzer()
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

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyzeImage(imageProxy: ImageProxy, rotationDegrees: Int): AnalysisResult {
        if (SafetyHelmetDetectionApplication.INSTANCE.module != null) {
            try {
                val results = objectInImageAnalyzer.analyze(
                    SafetyHelmetDetectionApplication.INSTANCE.module!!,
                    imageProxy.image ?: throw NullPointerException(),
                    boundingBoxDisplayView.width.toFloat(),
                    boundingBoxDisplayView.height.toFloat(),
                )
                return AnalysisResult(results)
            } catch(e: Exception) {
                Log.d("hanjungwoo", "eee", e)
            }
        }
        return AnalysisResult(listOf())
    }

    override fun applyToUiAnalyzeImageResult(result: AnalysisResult) {
        val labels = listOf("class 1", "class 2")
        boundingBoxDisplayView.boundingBoxes = result.objects.map { o ->
            BoundingBox(String.format("%s %.2f", "class " + o.classIndex, o.score), o.rect)
        }
        boundingBoxDisplayView.invalidate()
    }
}