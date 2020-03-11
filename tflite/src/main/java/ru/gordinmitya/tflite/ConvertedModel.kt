package ru.gordinmitya.tflite

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel

class ConvertedModel<T> private constructor(
    val model: T,
    val file: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
            "tflite/mobilenet_v2.tflite"
        )

        val deeplab_v3 = ConvertedModel(
            DeepLabModel,
            "tflite/deeplabv3.tflite"
        )

        fun <T : Model> getByModel(model: Model): ConvertedModel<T>? {
            @Suppress("UNCHECKED_CAST")
            return all.find {
                it.model == model
            } as ConvertedModel<T>?
        }

        val all = arrayListOf(
            mobilenet_v2,
            deeplab_v3
        )
    }
}