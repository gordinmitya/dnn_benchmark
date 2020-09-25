package ru.gordinmitya.dnnbenchmark.classification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.dnnbenchmark.utils.CyclicIterator
import java.io.File
import java.lang.AssertionError

class ModelAssets(val context: Context, val model: ClassificationModel) {
    private val samples: List<Asset>
    private var iterator: CyclicIterator<Asset>

    private val labels: List<String> = context.assets
        .open(model.labelsFile)
        .bufferedReader()
        .readLines()

    init {
        samples = getImagesInSubFolders(context, model.samplesDir)
        samples.forEach {
            if (it.label !in labels)
                throw AssertionError("image with unknown label ${it.label}")
        }

        iterator = CyclicIterator(samples)
    }

    fun getGT(): GT {
        val asset = iterator.next()
        val bitmap = loadImage(context, asset.image)
        val prob = loadProb(context, asset.prediction)
        if (prob.size != labels.size)
            throw AssertionError("prediction != number of classes for ${asset.prediction}")
        return GT(bitmap, asset.label, prob)
    }

    fun getLabelForPrediction(prediction: FloatArray): String {
        var index = 0
        for (i in prediction.indices) {
            if (prediction[i] > prediction[index])
                index = i
        }

        return labels[index]
    }

    private class Asset(folder: String, val label: String, name: String) {
        val image: String = "$folder/$label/$name$IMG_EXT"
        val prediction: String = "$folder/$label/$name$PROB_EXT"
    }

    companion object {
        const val IMG_EXT = ".jpeg"
        const val PROB_EXT = ".txt"

        private fun getImagesInSubFolders(context: Context, folder: String): List<Asset> {
            return context.assets
                .list(folder)
                ?.flatMap { label ->
                    context.assets
                        .list(File(folder, label).path)
                        ?.filter { it.contains(IMG_EXT) }
                        ?.map { it.dropLast(IMG_EXT.length) }
                        ?.map { name -> Asset(folder, label, name) } ?: emptyList()
                } ?: emptyList()
        }

        private fun loadImage(context: Context, path: String): Bitmap {
            return context.assets.open(path).use {
                BitmapFactory.decodeStream(it)
            }
        }

        private fun loadProb(context: Context, path: String): FloatArray {
            return context.assets.open(path).use { input ->
                input.bufferedReader()
                    .readLines()
                    .map { it.toFloat() }
                    .toFloatArray()
            }
        }
    }
}