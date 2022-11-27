package ru.gordinmitya.mnn

import android.os.Build
import com.taobao.android.mnn.MNNForwardType
import com.taobao.android.mnn.MNNNetNative
import ru.gordinmitya.common.InferenceType

sealed class MNNInferenceType(name: String, val type: Int, supported: Boolean) :
    InferenceType(name, supported)

object CPU : MNNInferenceType("CPU", MNNForwardType.FORWARD_CPU.type, true)

object OPEN_CL :
    MNNInferenceType("OpenCL", MNNForwardType.FORWARD_OPENCL.type, MNNNetNative.LOADED_CL)

object VULKAN :
    MNNInferenceType("Vulkan", MNNForwardType.FORWARD_VULKAN.type, MNNNetNative.LOADED_VULKAN)

object NN : MNNInferenceType("NN", MNNForwardType.FORWARD_NN.type, Build.VERSION.SDK_INT >= 27)
