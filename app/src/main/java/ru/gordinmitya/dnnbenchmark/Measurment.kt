package ru.gordinmitya.dnnbenchmark

import ru.gordinmitya.common.InferenceResult

class Measurment(
    val device: DeviceInfo,
    val results: List<InferenceResult>
)