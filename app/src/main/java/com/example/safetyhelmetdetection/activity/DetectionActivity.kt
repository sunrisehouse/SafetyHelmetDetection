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
//        SafetyHelmetDetectionApplication.INSTANCE.module
//        if (module != null) {
//            try {
//                val image: Image = imageProxy.image ?: throw NullPointerException()
//                var bitmap: Bitmap = imgToBitmap(image)
//                val matrix = Matrix()
//                matrix.postRotate(90.0f)
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//                val resizedBitmap = Bitmap.createScaledBitmap(
//                    bitmap,
//                    PrePostProcessor.mInputWidth,
//                    PrePostProcessor.mInputHeight,
//                    true
//                )
//
//                val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
//                    resizedBitmap,
//                    PrePostProcessor.NO_MEAN_RGB,
//                    PrePostProcessor.NO_STD_RGB
//                )
//                val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()
//                val outputTensor = outputTuple[0].toTensor()
//                val outputs = outputTensor.dataAsFloatArray
//
//                val imgScaleX: Float = bitmap.width.toFloat() / PrePostProcessor.mInputWidth
//                val imgScaleY: Float = bitmap.height.toFloat() / PrePostProcessor.mInputHeight
//                val ivScaleX = boundingBoxDisplayView.getWidth() as Float / bitmap.width
//                val ivScaleY = boundingBoxDisplayView.getHeight() as Float / bitmap.height
//
//                val results = PrePostProcessor.outputsToNMSPredictions(
//                    outputs,
//                    imgScaleX,
//                    imgScaleY,
//                    ivScaleX,
//                    ivScaleY,
//                    0.0f,
//                    0.0f,
//                )
//                return AnalysisResult(results)
//            } catch(e: Exception) {
//                Log.d("hanjungwoo", "eee", e)
//            }
//        }
//        return AnalysisResult(listOf())
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

    private fun imgToBitmap(image: Image): Bitmap {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}