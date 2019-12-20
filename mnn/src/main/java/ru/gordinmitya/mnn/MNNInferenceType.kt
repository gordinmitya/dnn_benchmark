package ru.gordinmitya.mnn

import com.taobao.android.mnn.MNNForwardType
import ru.gordinmitya.common.InferenceType

sealed class MNNInferenceType(name: String, val type: Int) : InferenceType(name) {}
object CPU : MNNInferenceType("CPU", MNNForwardType.FORWARD_CPU.type)
object VULKAN : MNNInferenceType("Vulkan", MNNForwardType.FORWARD_VULKAN.type)
object OPEN_CL : MNNInferenceType("OpenCL", MNNForwardType.FORWARD_OPENCL.type)
object OPEN_GL : MNNInferenceType("OpenGL", MNNForwardType.FORWARD_OPENGL.type)