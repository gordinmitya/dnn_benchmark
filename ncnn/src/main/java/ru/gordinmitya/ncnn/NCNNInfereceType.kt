package ru.gordinmitya.ncnn

import ru.gordinmitya.common.InferenceType

sealed class NCNNInfereceType(name: String, val gpu: Boolean, supported: Boolean) :
    InferenceType(name, supported)
object NCNN_CPU : NCNNInfereceType("CPU", false, true)
object NCNN_VULKAN : NCNNInfereceType("Vulkan", true, NCNNNative.isGpuAvailable())
