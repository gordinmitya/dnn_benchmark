package ru.gordinmitya.snpe

import com.qualcomm.qti.snpe.NeuralNetwork
import ru.gordinmitya.common.InferenceType

sealed class SNPEInfereceType(name: String, val runtime: NeuralNetwork.Runtime) :
    InferenceType(name)

object SNPE_CPU : SNPEInfereceType("CPU", NeuralNetwork.Runtime.CPU)
object SNPE_GPU : SNPEInfereceType("GPU", NeuralNetwork.Runtime.GPU)
object SNPE_GPU16 : SNPEInfereceType("GPU16", NeuralNetwork.Runtime.GPU_FLOAT16)
object SNPE_DSP : SNPEInfereceType("DSP", NeuralNetwork.Runtime.DSP)