package ru.gordinmitya.common

class Model private constructor(
    val name: String,
    val description: String
) {
    companion object {
        val mobilenet_v2 = Model("mobilenet_v2", "made by Google")

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}