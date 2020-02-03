package ru.gordinmitya.tf_mobile

import ru.gordinmitya.common.InferenceType

sealed class PytorchInfereceType(name: String) : InferenceType(name)
object PYTORCH_CPU : PytorchInfereceType("CPU")