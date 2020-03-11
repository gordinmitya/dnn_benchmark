package ru.gordinmitya.common.segmentation

import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.TaskExecutor

abstract class Segmentator(val configuration: Configuration) :
    TaskExecutor<Bitmap, FloatArray>()