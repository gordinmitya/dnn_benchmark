package ru.gordinmitya.dnnbenchmark.utils

import android.content.Context
import android.graphics.Bitmap
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

        samples = getImagesInSubFolders(context, model.samplesDir)
            .mapNotNull {
                if (it.first in labels)
                    it
                else {
                    Log.w("ModelAssets", "$it is not an label for this dataset!")
                    null
                }
            }

        iterator = CyclicIterator(samples)
    }

    fun getGT(): GT {
        val (label, file) = iterator.next()
        val bitmap = loadImage(context, file)
        return GT(bitmap, label)
    }

    fun getLabelForPrediction(prediction: FloatArray): String {
        require(labels.size == prediction.size)

        var index = 0
        for (i in prediction.indices) {
            if (prediction[i] > prediction[index])
                index = i
        }
        // FIXME use the same model for all frameworks
        // skip "background" label
        if (prediction.size == 1000)
            index += 1

        return labels[index]
    }

    companion object {
        fun getImagesInSubFolders(context: Context, folder: String): List<Pair<String, String>> {
            return context.assets
                .list(folder)
                ?.filter { !it.contains('.') }
                ?.flatMap { tag ->
                    val dir = File(folder, tag).path
                    val images = context.assets
                        .list(File(folder, tag).path)
                        ?.map { "$dir/$it" } ?: emptyList()
                    Array(images.size) { tag }.zip(images)
                } ?: emptyList()
        }

        fun loadImage(context: Context, path: String): Bitmap {
            return context.assets.open(path).use {
                BitmapFactory.decodeStream(it)
            }
        }
    }
}