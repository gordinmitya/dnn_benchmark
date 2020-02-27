package ru.gordinmitya.dnnbenchmark.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {
    const val PERMISSION_REQUEST = 1
    fun request(activity: Activity): Boolean {
        val want = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            want.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        val need = want.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (need.isEmpty())
            return true

        ActivityCompat.requestPermissions(activity, need, PERMISSION_REQUEST)
        return false
    }

    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST) {
            activity.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        val all = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (!all)
            request(activity)
    }
}