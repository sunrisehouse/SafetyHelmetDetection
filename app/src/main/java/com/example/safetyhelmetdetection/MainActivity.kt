package com.example.safetyhelmetdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

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