package com.example.wss_mock_app.audio

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}