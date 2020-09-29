package ru.gordinmitya.common.utils

import android.content.Context
import java.io.File

object AssetUtil {
    fun copyFileToCache(context: Context, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        if (file.parentFile?.exists() != true) {
            file.parentFile?.mkdirs()
        }
        context.assets.open(fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}