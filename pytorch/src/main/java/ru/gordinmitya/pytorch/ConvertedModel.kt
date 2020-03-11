package ru.gordinmitya.pytorch

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNetModel

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
            "mobilenet_v2.pt"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}