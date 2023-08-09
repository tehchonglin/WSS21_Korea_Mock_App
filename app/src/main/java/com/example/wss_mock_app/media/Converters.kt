package com.example.wss_mock_app.media

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun fromStringToUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}


