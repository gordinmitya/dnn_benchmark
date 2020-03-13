package ru.gordinmitya.dnnbenchmark.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Bitmap.save(context: Context): File {
    val name = UUID.randomUUID().toString() + ".png"
    val file = File(context.getExternalFilesDir(null), name)
    this.save(file)
    return file
}

fun Bitmap.save(file: File) {
    FileOutputStream(file).use { out ->
        this.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}