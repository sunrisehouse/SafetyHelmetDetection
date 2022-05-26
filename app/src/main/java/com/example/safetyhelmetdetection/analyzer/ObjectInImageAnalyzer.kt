package com.example.safetyhelmetdetection.analyzer

import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.safetyhelmetdetection.SafetyHelmetDetectionApplication
import com.example.safetyhelmetdetection.model.DetectionObject
import com.example.safetyhelmetdetection.processor.PrePostProcessor
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream

class ObjectInImageAnalyzer {
    fun analyze(
        module: Module,
        bitmapSrc: Bitmap,
        boundingBoxDisplayViewWidth: Float,
        boundingBoxDisplayViewHeight: Float
    ): List<DetectionObject> {
        val matrix = Matrix()
        matrix.postRotate(90.0f)
        val bitmap = Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.width, bitmapSrc.height, matrix, true)
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            PrePostProcessor.mInputWidth,
            PrePostProcessor.mInputHeight,
            true
        )

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            PrePostProcessor.NO_MEAN_RGB,
            PrePostProcessor.NO_STD_RGB
        )
        val outputTuple: Array<IValue> = module.forward(
            IValue.from(inputTensor)
        ).toTuple()
        val outputTensor = outputTuple[0].toTensor()
        val outputs = outputTensor.dataAsFloatArray

        val imgScaleX: Float = bitmap.width.toFloat() / PrePostProcessor.mInputWidth
        val imgScaleY: Float = bitmap.height.toFloat() / PrePostProcessor.mInputHeight
        val ivScaleX = boundingBoxDisplayViewWidth / bitmap.width
        val ivScaleY = boundingBoxDisplayViewHeight / bitmap.height

        return PrePostProcessor.outputsToNMSPredictions(
            outputs,
            imgScaleX,
            imgScaleY,
            ivScaleX,
            ivScaleY,
            0.0f,
            0.0f,
        )
    }
    fun imgToBitmap(image: Image): Bitmap {
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