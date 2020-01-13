package ru.gordinmitya.common.classification

import android.graphics.Bitmap
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task

open class ClassificationModel(
    name: String,
    description: String,
    val inputSize: Pair<Int, Int>,
    val labels: List<String>,
    val truths: List<GT>
) : Model(name, description, Task.CLASSIFICATION) {
    val outputSize: Int = labels.size

    fun getLabelByPrediction(prediction: FloatArray): String {
        require(labels.size == prediction.size)
        var index = 0
        for (i in prediction.indices) {
            if (prediction[i] > prediction[index])
                index = i
        }
        return labels[index]
    }

    override fun toString(): String = "$name (classification) $description"
}

object MobileNet_v2 : ClassificationModel(
    "mobilenet_v2",
    "made by Google",
    224 to 224,
    // FIXME real labels and examples are needed
    List(1001) { "cat" },
    listOf(
        GT(
            Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888),
            FloatArray(1001)
        )
    )
)