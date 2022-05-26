package com.example.safetyhelmetdetection

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class SafetyHelmetDetectionApplication: Application() {
    var module: Module? = null;

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        loadModule()
    }

    private fun loadModule() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val modelFileName = "best.torchscript.ptl"
                val context = applicationContext;
                val file = File(context.filesDir, modelFileName)
                if (!(file.exists() && file.length() > 0)) {
                    context.assets.open(modelFileName).use { `is` ->
                        FileOutputStream(file).use { os ->
                            val buffer = ByteArray(4 * 1024)
                            var read: Int
                            while (`is`.read(buffer).also { read = it } != -1) {
                                os.write(buffer, 0, read)
                            }
                            os.flush()
                        }
                    }
                }
                module = LiteModuleLoader.load(file.absolutePath)
            } catch (e: Exception) {
                Log.e("SHD", "Error reading assets", e)
            }
        }
    }

    companion object {
        lateinit var INSTANCE: SafetyHelmetDetectionApplication
    }
}