package com.example.wss_mock_app

import androidx.compose.ui.graphics.painter.Painter
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

data class EventDetails(
    val id: String,
    val Name: String,
    val Description: String,
    val DetailedDescription: String,
    val Status: Boolean,
    val Thumbnail: Painter,
    val Pictures: List<Painter>
)

@Entity(tableName = "opening_ticket_details")
data class OpeningTicketDetails(
    val Name: String,
    val Picture: ByteArray,
    val Date: String,
    val Seat: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

@Entity(tableName = "closing_ticket_details")
data class ClosingTicketDetails(
    val Name: String,
    val Picture: ByteArray,
    val Date: String,
    val Seat: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)