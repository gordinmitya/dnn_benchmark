package ru.gordinmitya.tf_mobile

import ru.gordinmitya.common.Model

class ConvertedModel private constructor(
    val model: Model,
    val inputSize: Pair<Int, Int>,
    val inputName: String,
    val outputName: String,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            Model.mobilenet_v2,
            224 to 224,
            "input",
            "MobilenetV2/Predictions/Reshape_1",
            "mobilenet_v2.pb"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}