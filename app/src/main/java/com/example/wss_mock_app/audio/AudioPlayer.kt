package com.example.wss_mock_app.audio

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}