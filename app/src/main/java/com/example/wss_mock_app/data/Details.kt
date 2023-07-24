package com.example.wss_mock_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Picture(
    val url: String,
    val description: String
)

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val status: Boolean,
    val thumbnail: String,
    val pictures: List<Picture>
)

data class EventDetails(
    val EventDetails: List<Event>
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