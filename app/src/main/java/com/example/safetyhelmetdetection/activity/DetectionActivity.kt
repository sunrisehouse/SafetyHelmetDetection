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
import android.graphics.*
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class DetectionActivity : CameraXActivity() {
    private lateinit var boundingBoxDisplayView: BoundingBoxDisplayView
    private val objectInImageAnalyzer = ObjectInImageAnalyzer()
    private lateinit var cameraPreviewView: PreviewView
    class AnalysisResult(val objects: List<DetectionObject>)
    private lateinit var cautionRingtone: Ringtone
    private val HELMET_CLASS_ID = 1

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection)
        boundingBoxDisplayView = findViewById(R.id.boundingBoxDisplayView)
        cameraPreviewView = findViewById<PreviewView>(R.id.cameraPreviewView)

        val uriNotification = Uri.parse("android.resource://com.example.safetyhelmetdetection/"+R.raw.wear_a_safety_helmet)
        cautionRingtone = RingtoneManager.getRingtone(applicationContext, uriNotification)
    }

    override fun buildPreview(): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(cameraPreviewView.surfaceProvider)
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyzeImage(imageProxy: ImageProxy, rotationDegrees: Int) {
        if (SafetyHelmetDetectionApplication.INSTANCE.module != null) {
            try {
                val bitmap = objectInImageAnalyzer.imgToBitmap(
                    imageProxy.image ?: throw NullPointerException()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val results = objectInImageAnalyzer.analyze(
                        SafetyHelmetDetectionApplication.INSTANCE.module!!,
                        bitmap,
                        boundingBoxDisplayView.width.toFloat(),
                        boundingBoxDisplayView.height.toFloat(),
                    )
                    runOnUiThread { applyToUiAnalyzeImageResult(AnalysisResult(results)) }
                    val headIndex = results.indexOfFirst { c -> c.classIndex != HELMET_CLASS_ID }
                    Log.d("hanjungwoo", "sssss: " + headIndex)
                    if (headIndex != -1 && !cautionRingtone.isPlaying) {
                        cautionRingtone.play()
                    }
                    else if (headIndex == -1 && cautionRingtone.isPlaying){
                        cautionRingtone.stop()
                    }
                }
            } catch(e: Exception) {
                Log.e("SHD", "Analyze Failed", e)
            }
        }
    }

    private fun applyToUiAnalyzeImageResult(result: AnalysisResult) {
        boundingBoxDisplayView.boundingBoxes = result.objects.map { o ->
            BoundingBox(String.format("%s (%.2f)", if (o.classIndex == HELMET_CLASS_ID) "Helmet" else "Head", o.score), o.rect, if (o.classIndex == 1) Color.RED else Color.YELLOW)
        }
        boundingBoxDisplayView.invalidate()
    }
}