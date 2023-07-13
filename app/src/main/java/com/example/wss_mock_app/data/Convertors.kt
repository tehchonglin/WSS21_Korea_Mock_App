package com.example.wss_mock_app.data

import android.net.Uri
import android.os.Environment
import androidx.room.TypeConverter
import java.io.File

class Converters{
    @TypeConverter
    fun fromUriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun fromStringToUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun fromByteArray(value: ByteArray?): File? {
        return value?.let {
            //Replace Audio.amr with your filename and extension
            val path = Environment.getExternalStorageDirectory().path+"/Audio.amr"
            val file = File(path)
            file.writeBytes(value)
            file
        }
    }

    @TypeConverter
    fun toByteArray(file: File?): ByteArray? {
        return file?.readBytes()
    }
}


