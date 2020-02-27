package ru.gordinmitya.dnnbenchmark.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.dnnbenchmark.classification.GT
import java.io.File

class ModelAssets(val context: Context, val model: ClassificationModel) {
    val labels: List<String>
    val samples: List<Pair<String, String>>
    var iterator: CyclicIterator<Pair<String, String>>

    init {
        labels = context.assets
            .open(model.labelsFile)
            .bufferedReader()
            .readLines()

        samples = context.assets
            .list(model.samplesDir)
            ?.filter { !it.contains('.') }
            ?.mapNotNull {
                if (it in labels)
                    it
                else {
                    Log.w("ModelAssets", "$it is not an label for this dataset!")
                    null
                }
            }
            ?.flatMap { tag ->
                val folder = File(model.samplesDir, tag).path
                val images = context.assets
                    .list(File(model.samplesDir, tag).path)
                    ?.map { "$folder/$it" } ?: emptyList()
                Array(images.size) { tag }.zip(images)
            } ?: emptyList()

        iterator = CyclicIterator(samples)
    }

    fun getGT(): GT {
        val (label, file) = iterator.next()
        val bitmap = context.assets.open(file).use {
            BitmapFactory.decodeStream(it)
        }
        return GT(bitmap, label)
    }

    fun getLabelForPrediction(prediction: FloatArray): String {
        // FIXME use the same model for all frameworks
//        require(labels.size == prediction.size)

        var index = 0
        for (i in prediction.indices) {
            if (prediction[i] > prediction[index])
                index = i
        }
        // FIXME skip "background" label
        if (prediction.size == 1000)
            index += 1

        return labels[index]
    }
}