package ru.gordinmitya.dnnbenchmark

import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.gordinmitya.common.BenchmarkResult
import java.io.IOException


class ResultsSender {
    val CONTENT_TYPE = "application/json; charset=utf-8".toMediaType()
    val URL = "https://bench.gordinmitya.ru/api/v1/measurements/"

    fun send(measurment: Measurment): Boolean {
        val gson = GsonBuilder().create()
        val json = gson.toJson(measurment)

        val body: RequestBody = json.toRequestBody(CONTENT_TYPE)
        val request: Request = Request.Builder()
            .url(URL)
            .post(body)
            .build()

        val okhttp = OkHttpClient()
        try {
            okhttp.newCall(request).execute().use {
                return it.isSuccessful && it.code == 201
            }
        } catch (e: IOException) {
            return false
        }
    }
}