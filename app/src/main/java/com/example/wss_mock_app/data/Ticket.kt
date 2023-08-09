package com.example.wss_mock_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey


data class TicketState(
    val tickets: List<TicketDetails> = emptyList(),
    var ticketType: String = "",
    var Name: String = "",
    var Picture: String = "",
    var Time: String = "",
    var Seat: String = "",
    var id: Int = 0
)

@Entity(tableName = "ticket_details")
data class TicketDetails(
    val ticketType: String,
    val Name: String,
    val Picture: String,
    val Time: String,
    val Seat: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)