package com.example.wss_mock_app.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun saveTemporaryFile(file: File, applicationContext: Context): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    val fileName = "File_${currentDateTime.format(formatter)}"
    val byteArray = file.readBytes()
    val resolver = applicationContext.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
    }
    val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
    resolver.openOutputStream(uri!!)?.use { it.write(byteArray) }
    return uri.toString()
}

fun uriToFile(uri: Uri, context: Context): File {
    val destinationFile = File(context.cacheDir, "tempFile")
    val inputStream = context.contentResolver.openInputStream(uri)
    inputStream?.use { input ->
        FileOutputStream(destinationFile).use { output ->
            input.copyTo(output)
        }
    }
    return destinationFile
}
