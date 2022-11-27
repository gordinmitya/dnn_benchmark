package ru.gordinmitya.common

import android.os.Process
import android.util.Log
import java.io.*

object NativeHelper {
    private fun processName(): String {
        val path = "/proc/" + Process.myPid() + "/cmdline"
        BufferedReader(InputStreamReader(FileInputStream(path), "iso-8859-1")).use { reader ->
            var c: Int
            val processName = StringBuilder()
            while (reader.read().also { c = it } > 0) {
                processName.append(c.toChar())
            }
            return processName.toString()
        }
    }

    // adb logcat -c && adb logcat | grep "LOAD_LIBRARY"
    @JvmStatic
    fun loadLibrary(libname: String?) {
        Log.d(
            "LOAD_LIBRARY",
            String.format("%s %s", libname, processName())
        )
        System.loadLibrary(libname!!)
    }
}
