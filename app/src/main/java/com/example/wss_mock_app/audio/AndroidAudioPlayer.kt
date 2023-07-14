package com.example.wss_mock_app.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private var player: MediaPlayer? = null

    override fun playFile(file: File) {
        stop()
        player = player ?: MediaPlayer()
        player?.apply {
            setDataSource(context, file.toUri())
            prepare()
            start()

            setOnCompletionListener {
                // This block will be executed when the audio finishes playing
                stop()
            }
        }
    }

    override fun stop() {
        player?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        player = null
    }

}