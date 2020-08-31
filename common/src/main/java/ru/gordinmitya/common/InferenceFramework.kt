package ru.gordinmitya.common

abstract class InferenceFramework(
    val name: String,
    val description: String
) {
    abstract fun getModels(): List<Model>
    abstract fun getInferenceTypes(): List<InferenceType>

    override fun toString(): String = name
}
