package ru.gordinmitya.onnxruntime

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel

class ConvertedModel private constructor(
    val model: ClassificationModel,
    val inputName: String,
    val outputName: String,
    val fileName: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
            "input",
            "473",
            "mobilenet_v2.onnx",
            // from https://github.com/Rohithkvsp/OnnxRuntimeAndorid
//            "mobilenet_v2-opt.onnx",
        )

        fun getByModel(model: Model): ConvertedModel? {
            return all.find { it.model == model }
        }

        val all = arrayListOf(
            mobilenet_v2
        )
    }
}