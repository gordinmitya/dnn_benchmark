package ru.gordinmitya.dnnbenchmark.utils

import java.lang.StringBuilder

typealias TextLoggerUpdateDelegate = (String) -> Unit

class TextLogger(val onUpdate: TextLoggerUpdateDelegate? = null) {
    private val sb = StringBuilder()
    private var spoilerLength = 0

    fun log(message: String) {
        removeSpoiler()
        sb.appendLine(message)
        update()
    }

    fun log(obj: Any) = log(obj.toString())

    fun spoiler(message: String) {
        removeSpoiler()
        spoilerLength = message.length
        sb.appendLine(message)
        update()
    }

    fun drawLine() {
        removeSpoiler()
        sb.appendLine(lineString)
        update()
    }

    fun clear() {
        sb.clear()
        update()
    }

    fun getText() = sb.toString()

    private fun update() {
        onUpdate?.invoke(getText())
    }

    private fun removeSpoiler() {
        if (spoilerLength > 0) {
            sb.delete(sb.length - spoilerLength - 1, sb.length)
        }
        spoilerLength = 0
    }

    private val lineString = "–".repeat(8)
}
