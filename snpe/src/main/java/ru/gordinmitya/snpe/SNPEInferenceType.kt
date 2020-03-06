package ru.gordinmitya.snpe

import android.app.Application
import com.qualcomm.qti.snpe.NeuralNetwork
import com.qualcomm.qti.snpe.SNPE
import ru.gordinmitya.common.InferenceType

sealed class SNPEInferenceType(
    application: Application,
    name: String,
    val runtime: NeuralNetwork.Runtime
) : InferenceType(
    name,
    true
//    SNPE.NeuralNetworkBuilder(application)
//        .isRuntimeSupported(runtime)
)

class SNPE_CPU(application: Application) : SNPEInferenceType(
    application, "CPU", NeuralNetwork.Runtime.CPU
)

class SNPE_GPU(application: Application) : SNPEInferenceType(
    application, "GPU", NeuralNetwork.Runtime.GPU
)

class SNPE_GPU16(application: Application) : SNPEInferenceType(
    application, "GPU16", NeuralNetwork.Runtime.GPU_FLOAT16
)

class SNPE_DSP(application: Application) : SNPEInferenceType(
    application, "DSP", NeuralNetwork.Runtime.DSP
)

class SNPE_AIP(application: Application) : SNPEInferenceType(
    application, "AIP", NeuralNetwork.Runtime.AIP
)