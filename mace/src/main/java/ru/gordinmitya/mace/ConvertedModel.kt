package ru.gordinmitya.mace

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.MobileNet_v2

class ConvertedModel private constructor(
    val model: Model,
    val inputName: String,
    val outputName: String,
    val pbFileName: String,
    val dataFileName: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNet_v2,
            "input",
            "MobilenetV2/Predictions/Reshape_1",
            "mace/mobilenet_v2.pb",
            "mace/mobilenet_v2.data"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}