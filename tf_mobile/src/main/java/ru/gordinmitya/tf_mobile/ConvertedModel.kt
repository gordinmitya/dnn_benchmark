package ru.gordinmitya.tf_mobile

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNet_v2

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val inputName: String,
    val outputName: String,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNet_v2,
            "input",
            "MobilenetV2/Predictions/Reshape_1",
            "mobilenet_v2.pb"
        )

        fun getByModel(model: Model): ConvertedModel? {

            Byte.hashCode()



            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}