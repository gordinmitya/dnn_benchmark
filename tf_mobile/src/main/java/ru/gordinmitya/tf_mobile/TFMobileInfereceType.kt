package ru.gordinmitya.tf_mobile

import ru.gordinmitya.common.InferenceType

sealed class TFMobileInfereceType(name: String) : InferenceType(name)
object TF_MOBILE_CPU : TFMobileInfereceType("CPU")