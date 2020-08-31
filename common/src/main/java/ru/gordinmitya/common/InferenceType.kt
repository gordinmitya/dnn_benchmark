package ru.gordinmitya.common

open class InferenceType(
    val name: String,
    // eg DSP only available on Snapdragon
    val isSupported: Boolean = true
) {
    override fun toString(): String = name
}