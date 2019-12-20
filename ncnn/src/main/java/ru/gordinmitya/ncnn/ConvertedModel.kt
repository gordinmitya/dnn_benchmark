package ru.gordinmitya.ncnn

import ru.gordinmitya.common.Model

class ConvertedModel private constructor(
    val model: Model,
    val inputSize: Pair<Int, Int>,
    val inputName: String,
    val outputName: String,
    val paramFile: String,
    val binFile: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            Model.mobilenet_v2,
            224 to 224,
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