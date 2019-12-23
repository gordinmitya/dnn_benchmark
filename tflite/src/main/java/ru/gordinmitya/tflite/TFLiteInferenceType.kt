package ru.gordinmitya.tflite

import android.os.Build
import ru.gordinmitya.common.InferenceType

sealed class TFLiteInferenceType(name: String, isSupported: Boolean) :
    InferenceType(name, isSupported)

object TFLITE_CPU : TFLiteInferenceType("CPU", true)
object TFLITE_OPENGL : TFLiteInferenceType("GPU", true)
object TFLITE_NNAPI : TFLiteInferenceType("NNAPI", Build.VERSION.SDK_INT >= 27)