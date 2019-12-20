package ru.gordinmitya.ncnn

import ru.gordinmitya.common.InferenceType

sealed class NCNNInfereceType(name: String, val gpu: Boolean) : InferenceType(name)
object NCNN_CPU : NCNNInfereceType("CPU", false)
object NCNN_VULKAN : NCNNInfereceType("Vulkan", true)