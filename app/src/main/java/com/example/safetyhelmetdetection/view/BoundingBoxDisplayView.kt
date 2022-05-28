package com.example.safetyhelmetdetection.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.safetyhelmetdetection.model.BoundingBox
import com.example.safetyhelmetdetection.model.DetectionObject

/**
 * TODO: document your custom view class.
 */
class BoundingBoxDisplayView : View {
    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }

    private val mPaintRectangle: Paint = Paint()
    private val mPaintText: Paint
    var boundingBoxes: List<BoundingBox>? = null

    init {
        mPaintRectangle.strokeWidth = 5f
        mPaintRectangle.style = Paint.Style.STROKE
        mPaintText = Paint()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (boundingBoxes == null) return
        for ((label, rect, color) in boundingBoxes!!) {
            mPaintRectangle.color = color
            canvas.drawRect(rect, mPaintRectangle)
            val mPath = Path()
            val mRectF = RectF(
                rect.left.toFloat(),
                rect.top.toFloat(),
                (rect.left + TEXT_WIDTH).toFloat(),
                (rect.top + TEXT_HEIGHT).toFloat()
            )
            mPath.addRect(mRectF, Path.Direction.CW)
            mPaintText.color = Color.MAGENTA
            canvas.drawPath(mPath, mPaintText)
            mPaintText.color = Color.WHITE
            mPaintText.strokeWidth = 0f
            mPaintText.style = Paint.Style.FILL
            mPaintText.textSize = 32f
            canvas.drawText(
                label,
                (rect.left + TEXT_X).toFloat(),
                (rect.top + TEXT_Y).toFloat(),
                mPaintText
            )
        }
    }
}