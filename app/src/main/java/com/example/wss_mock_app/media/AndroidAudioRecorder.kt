package com.example.wss_mock_app.media

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(private val context: Context) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        val recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }
        this.recorder = recorder
        return recorder
    }
    override fun start(outputFile: File) {
        // If an instance of the recorder already exists, we stop, reset and nullify it before creating a new one
        stop()
        val recorder = createRecorder()
        recorder.setOutputFile(FileOutputStream(outputFile).fd)

        recorder.apply {
            try {
                prepare()
                start()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                this@AndroidAudioRecorder.recorder = null
            }
        }
    }

    override fun stop() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                recorder = null
            }
        }
    }
}
