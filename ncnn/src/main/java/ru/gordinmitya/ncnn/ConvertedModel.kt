package ru.gordinmitya.ncnn

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNet_v2

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val inputName: String,
    val outputName: String,
    val paramFile: String,
    val binFile: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNet_v2,
            "input.1",
            "465",
            "mobilenet_v2/ncnn.param",
            "mobilenet_v2/ncnn.bin"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}