package ru.gordinmitya.common

class Version(
    val name: String,
    val commitHash: String? = null,
) {
    override fun toString(): String {
        if (commitHash == null) return name
        return "$name ($commitHash)"
    }
}

abstract class InferenceFramework(
    val name: String,
    val version: Version
) {
    abstract fun getModels(): List<Model>
    abstract fun getInferenceTypes(): List<InferenceType>

    override fun toString(): String = name
}
