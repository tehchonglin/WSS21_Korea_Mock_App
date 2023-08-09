package com.example.wss_mock_app.media

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}