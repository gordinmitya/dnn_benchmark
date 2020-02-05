package ru.gordinmitya.pytorch

import ru.gordinmitya.common.InferenceType

sealed class PytorchInfereceType(name: String) : InferenceType(name)
object PYTORCH_CPU : PytorchInfereceType("CPU")