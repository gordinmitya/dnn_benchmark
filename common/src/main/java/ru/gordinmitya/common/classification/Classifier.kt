package ru.gordinmitya.common.classification

import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration

abstract class Classifier(val configuration: Configuration) {
    /**
     * Prepare inference framework.
     * Create an instance, load weights, etc.
     */
    abstract fun prepare()

    /**
     * Performs inference of a network and returns results
     * @param bitmap input image in format ARGB_8888
     * @return probabilities of classes
     */
    abstract fun predict(bitmap: Bitmap): FloatArray

    /**
     * Frees all used resources.
     * Should reset the system to blank state as it was before prepare call.
     */
    abstract fun release()
}