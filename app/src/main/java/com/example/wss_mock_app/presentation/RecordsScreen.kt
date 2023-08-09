package com.example.wss_mock_app.presentation

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wss_mock_app.R
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.FontScalePreview
import com.example.wss_mock_app.media.AndroidAudioPlayer
import com.example.wss_mock_app.media.AndroidAudioRecorder
import com.example.wss_mock_app.data.AudioState
import com.example.wss_mock_app.media.saveTemporaryFile
import com.example.wss_mock_app.media.uriToFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordsScreen(
    applicationContext: Context,
    audioState: AudioState,
    onEvent: (TicketEvent) -> Unit
) {
    var recordingState by remember { mutableStateOf("stopped") }
    var playingState by remember { mutableStateOf("stopped") }
    var filePath by remember { mutableStateOf("") }
    val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }
    val player by lazy {
        AndroidAudioPlayer(applicationContext) {
            playingState = "stopped"
        }
    }
    var audioFile: File? = null
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Records",
                fontSize = 35.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 40.dp, 0.dp, 30.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (permissionState.status.isGranted) {
                                    recordingState = if (recordingState == "stopped") {
                                        File(applicationContext.cacheDir, "audio.mp3").also {
                                            recorder.start(it)
                                            audioFile = it
                                        }
                                        "recording"
                                    } else {
                                        recorder.stop()
                                        Log.d("Audio Status", audioFile.toString())
                                        "stopped"
                                    }
                                } else {
                                    permissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            shape = RectangleShape
                        ) {
                            val text =
                                if (recordingState == "stopped") "Voice Recording" else "Stop Recording"
                            Text(text = text)
                        }
                        Button(
                            onClick = {
                                if (playingState == "stopped") {
                                    player.playFile(audioFile ?: return@Button)
                                    playingState = "playing"
                                } else {
                                    player.stop()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            shape = RectangleShape
                        ) {
                            val text =
                                if (playingState == "stopped") "Voice Play" else "Stop playing"
                            Text(text = text)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.size(0.dp, 100.dp))
                        Button(
                            onClick = {
                                audioFile?.let {
                                    filePath = saveTemporaryFile(it, applicationContext)
                                }
                                onEvent(TicketEvent.SetFile(filePath))
                                onEvent(TicketEvent.SaveAudio)
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "Submit",
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 30.dp),
                shape = RectangleShape
            ) {
                Text(
                    text = "Audios List",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(audioState.audioDetails) { state ->
                        Card(modifier = Modifier.padding(5.dp, 10.dp)) {
                            var buttonIcon by remember { mutableStateOf(R.drawable.baseline_play_arrow_24) }
                            var individualPlayingState by remember { mutableStateOf("stopped") }
                            val individualPlayer by lazy {
                                AndroidAudioPlayer(applicationContext) {
                                    individualPlayingState = "stopped"
                                }
                            }
                            Row {
                                Text(
                                    text = "Audio ${state.id}",
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
                                )
                                Button(onClick = {
                                    if (individualPlayingState == "stopped") {
                                        val uri = Uri.parse(state.audio)
                                        val file = uriToFile(uri, applicationContext)
                                        individualPlayer.playFile(file)
                                        individualPlayingState = "playing"
                                    } else {
                                        individualPlayer.stop()
                                    }
                                }) {
                                    buttonIcon =
                                        if (individualPlayingState == "stopped") R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24
                                    Icon(
                                        painterResource(id = buttonIcon),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
//@Pixel2Preview
//@PixelCPreview
@FontScalePreview
fun RecordScreenPreview() {
    val audioState = AudioState()
    audioState.id = 5
    RecordsScreen(LocalContext.current, audioState, {})
}