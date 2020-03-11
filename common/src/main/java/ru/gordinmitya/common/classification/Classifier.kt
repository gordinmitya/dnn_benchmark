package ru.gordinmitya.common.classification

import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.TaskExecutor

abstract class Classifier(val configuration: Configuration) :
    TaskExecutor<Bitmap, FloatArray>()