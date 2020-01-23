package ru.gordinmitya.common.classification

import android.graphics.Bitmap

class GT(val image: Bitmap, val label: String, val probabilities: FloatArray? = null)