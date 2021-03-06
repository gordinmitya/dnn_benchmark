package ru.gordinmitya.mnn

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel

class ConvertedModel<T> private constructor(
    val model: T,
    val fileName: String
) {
    companion object {
        val mobilenet_v2 = ConvertedModel(
            MobileNetModel,
            "mnn/mobilenet_v2.mnn"
        )

        val deeplab_v3 = ConvertedModel(
            DeepLabModel,
            "mnn/deeplab_v3.mnn"
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