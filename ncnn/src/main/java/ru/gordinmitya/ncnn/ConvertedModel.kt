package ru.gordinmitya.ncnn

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNetModel

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val inputName: String,
    val outputName: String,
    val paramFile: String,
    val binFile: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
            "input",
            "473",
            "ncnn/mobilenet_v2-opt.param",
            "ncnn/mobilenet_v2-opt.bin"
//            "ncnn/mobilenet_v2.param",
//            "ncnn/mobilenet_v2.bin"
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}