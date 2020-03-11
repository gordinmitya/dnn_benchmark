package ru.gordinmitya.opencv

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNetModel

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val inputName: String,
    val outputName: String,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
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