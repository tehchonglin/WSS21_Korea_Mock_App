package com.example.wss_mock_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "audio")
data class Audio(
    val audio: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

data class AudioState(
    val audioDetails: List<Audio> = emptyList(),
    var audioFile: String = "",
    var id: Int = 0
)
