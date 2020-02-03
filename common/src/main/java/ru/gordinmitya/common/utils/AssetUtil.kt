package ru.gordinmitya.common.utils

import android.content.Context
import java.io.File

object AssetUtil {
    public fun copyFileToCache(context: Context, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        if (file.exists())
            return file
        context.assets.open(fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}