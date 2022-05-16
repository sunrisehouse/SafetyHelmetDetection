package com.example.safetyhelmetdetection.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.safetyhelmetdetection.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.mainStartButton)

        startButton.setOnClickListener {
            val nextIntent = Intent(this, DetectionActivity::class.java)
            startActivity(nextIntent)
        }
    }
}