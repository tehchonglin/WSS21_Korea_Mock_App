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

@Entity (tableName = "ticket_details")
data class TicketDetails(
    val ticketType: String,
    val Name: String,
    val Picture: ByteArray,
    val Time: String,
    val Seat: String,
    val order_id: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)