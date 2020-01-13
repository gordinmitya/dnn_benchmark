package ru.gordinmitya.tflite

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNet_v2

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNet_v2,
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