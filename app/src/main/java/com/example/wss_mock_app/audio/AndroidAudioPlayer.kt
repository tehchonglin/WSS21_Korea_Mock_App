package com.example.wss_mock_app.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(
    private val context: Context,
    onStopped: () -> Unit // We add the callback here
) : AudioPlayer {

    private var player: MediaPlayer? = null
    private val onStoppedCallback = onStopped

    override fun playFile(file: File) {
        stop()
        player = MediaPlayer().apply {
            setDataSource(context, file.toUri())
            prepare()
            start()
            setOnCompletionListener {
                onStoppedCallback.invoke()
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