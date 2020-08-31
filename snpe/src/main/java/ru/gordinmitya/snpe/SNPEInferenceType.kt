package ru.gordinmitya.snpe

import android.app.Application
import com.qualcomm.qti.snpe.NeuralNetwork
import com.qualcomm.qti.snpe.SNPE
import ru.gordinmitya.common.InferenceType

sealed class SNPEInferenceType(
    name: String,
    val runtime: NeuralNetwork.Runtime
) : InferenceType(
    name,
    true
)

/*
 in order to check if inference type is actually available
 there's a function
     SNPE.NeuralNetworkBuilder(application).isRuntimeSupported(runtime)
 but instead of actually return false on unsupported devices it just freeze application!
 */


class SNPE_CPU : SNPEInferenceType(
    "CPU", NeuralNetwork.Runtime.CPU
)

class SNPE_GPU : SNPEInferenceType(
    "GPU", NeuralNetwork.Runtime.GPU
)

class SNPE_GPU16 : SNPEInferenceType(
    "GPU16", NeuralNetwork.Runtime.GPU_FLOAT16
)

class SNPE_DSP : SNPEInferenceType(
    "DSP", NeuralNetwork.Runtime.DSP
)

class SNPE_AIP : SNPEInferenceType(
    "AIP", NeuralNetwork.Runtime.AIP
)