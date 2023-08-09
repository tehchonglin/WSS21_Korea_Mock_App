package com.example.wss_mock_app.media

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}