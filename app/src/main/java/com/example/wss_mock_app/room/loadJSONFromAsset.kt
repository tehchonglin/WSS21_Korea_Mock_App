package com.example.wss_mock_app.room

import android.content.Context
import java.io.IOException
import java.io.InputStream

fun loadJSONFromAsset(context: Context, filename: String): String? {
    var json = ""
    try {
        val inputStream: InputStream = context.assets.open(filename)
        json = inputStream.bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    return json
}