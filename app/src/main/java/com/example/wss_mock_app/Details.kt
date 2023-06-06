package com.example.wss_mock_app

import androidx.compose.ui.graphics.painter.Painter


data class EventDetails(
    val id: String,
    val Name: String,
    val Description: String,
    val DetailedDescription: String,
    val Status: Boolean,
    val Thumbnail: Painter,
    val Pictures: List<Painter>
)

data class TicketDetails(
    val id: String,
    val Name: String,
    val Picture: Painter,
    val Type: String,

)