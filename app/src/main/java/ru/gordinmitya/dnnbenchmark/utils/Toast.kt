package ru.gordinmitya.dnnbenchmark.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(message: Any, length: Int = Toast.LENGTH_LONG) {
    this.toast(message.toString(), length)
}

fun Context.toast(text: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, length).show()
}