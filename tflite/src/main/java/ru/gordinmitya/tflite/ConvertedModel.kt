package ru.gordinmitya.tflite

import ru.gordinmitya.common.Model

class ConvertedModel private constructor(
    val model: Model,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            Model.mobilenet_v2,
            "mobilenet_v2.tflite"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}