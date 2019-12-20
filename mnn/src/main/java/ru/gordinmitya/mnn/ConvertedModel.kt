package ru.gordinmitya.mnn

import ru.gordinmitya.common.Model

class ConvertedModel private constructor(
    val model: Model,
    val outputName: String,
    val fileName: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            Model.mobilenet_v2,
            "MobilenetV2/Predictions/Reshape_1",
            "mobilenet_v2.mnn"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}