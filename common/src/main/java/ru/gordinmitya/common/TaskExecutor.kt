package ru.gordinmitya.common

abstract class TaskExecutor<TIn, TOut> {
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
    abstract fun predict(input: TIn): TOut

    /**
     * Frees all used resources.
     * Should reset the system to blank state as it was before prepare call.
     *
     * Note can be called even if prepare failed
     */
    abstract fun release()
}