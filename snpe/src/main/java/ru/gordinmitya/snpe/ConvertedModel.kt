package ru.gordinmitya.snpe

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
            "input:0",
            "MobilenetV2/Predictions/Reshape_1:0",
            "snpe/mobilenet_v2.dlc"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}