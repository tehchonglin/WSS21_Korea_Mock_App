package com.example.wss_mock_app

import android.graphics.drawable.PaintDrawable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

@Dao
interface TicketDao{
    @Upsert
    suspend fun upsertOpeningTicket(ticketDetails: OpeningTicketDetails)

    @Upsert
    suspend fun upsertClosingTicket(ticketDetails: ClosingTicketDetails)

    @Delete
    suspend fun deleteOpeningTicket(ticketDetails: OpeningTicketDetails)

    @Delete
    suspend fun deleteClosingTicket(ticketDetails: ClosingTicketDetails)

    @Query("SELECT * FROM closing_ticket_details ORDER BY id ASC")
    fun getClosingTicketsOrderedByID(): Flow<List<ClosingTicketDetails>>

    @Query("SELECT * FROM opening_ticket_details ORDER BY id ASC")
    fun getOpeningTicketsOrderedByID(): Flow<List<OpeningTicketDetails>>
}

sealed interface TicketEvent {
    object SaveOpeningTicket: TicketEvent
    object SaveClosingTicket: TicketEvent
    data class SetName(val Name: String): TicketEvent
    data class SetSeat(val Seat: String): TicketEvent
    data class DeleteClosingTickets(val ticketDetails: ClosingTicketDetails): TicketEvent
    data class DeleteOpeningTickets(val ticketDetails: OpeningTicketDetails): TicketEvent
    data class SortOpeningTickets(val ticketDetails: OpeningTicketDetails): TicketEvent
    data class SortClosingTickets(val ticketDetails: ClosingTicketDetails): TicketEvent
}

data class OpeningTicketState(
    val tickets: List<OpeningTicketDetails> = emptyList(),
    val Name: String = "",
    val Picture: ByteArray? = null,
    val Date: Calendar = Calendar.getInstance(),
    val Seat: String = ""
)

data class ClosingTicketState(
    val tickets: List<ClosingTicketDetails> = emptyList(),
    val Name: String = "",
    val Picture: ByteArray? = null,
    val Date: Calendar = Calendar.getInstance(),
    val Seat: String = ""
)