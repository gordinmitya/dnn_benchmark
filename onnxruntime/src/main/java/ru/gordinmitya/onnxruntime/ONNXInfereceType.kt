package ru.gordinmitya.onnxruntime

import ru.gordinmitya.common.InferenceType

sealed class ONNXInfereceType(name: String, val gpu: Boolean) : InferenceType(name)
object ONNX_CPU : ONNXInfereceType("CPU", false)
//object ONNX_NNAPI : ONNXInfereceType("NNAPI", true)