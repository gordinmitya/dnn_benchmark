package ru.gordinmitya.mace

import ru.gordinmitya.common.InferenceType

/*

mace.h
enum DeviceType { CPU = 0, GPU = 2, HEXAGON = 3, HTA = 4, APU = 5 };

 */

sealed class MACEInferenceType(name: String, val type: Int) : InferenceType(name)
object CPU : MACEInferenceType("CPU",0)
object OPEN_CL : MACEInferenceType("OpenCL", 2)

// DSP, is it possible to do in prod?
// https://github.com/XiaoMi/mace/blob/master/docs/faq.md#why-is-mace-not-working-on-dsp
//object HEXAGON : MACEInferenceType("HEXAGON", 3)