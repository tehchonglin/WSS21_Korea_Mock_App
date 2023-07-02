package com.example.wss_mock_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters{
    @TypeConverter
    fun fromUriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun fromStringToUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}


