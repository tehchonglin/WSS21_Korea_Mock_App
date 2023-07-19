package com.example.wss_mock_app.data

import androidx.compose.ui.graphics.painter.Painter
import androidx.room.Entity
import androidx.room.PrimaryKey

data class EventDetails(
    val id: String,
    val Name: String,
    val Description: String,
    val DetailedDescription: String,
    val Status: Boolean,
    val Thumbnail: Painter,
    val Pictures: List<Painter>
)

@Entity (tableName = "ticket_details")
data class TicketDetails(
    val ticketType: String,
    val Name: String,
    val Picture: String,
    val Time: String,
    val Seat: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Entity (tableName = "audio")
data class Audio(
    val audio: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)