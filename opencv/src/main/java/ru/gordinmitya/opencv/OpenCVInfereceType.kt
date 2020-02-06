package ru.gordinmitya.opencv

import ru.gordinmitya.common.InferenceType

sealed class OpenCVInfereceType(name: String) : InferenceType(name)
object OPENCV_CPU : OpenCVInfereceType("CPU")