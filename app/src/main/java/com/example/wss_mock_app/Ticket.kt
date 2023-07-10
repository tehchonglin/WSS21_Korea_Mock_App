package com.example.wss_mock_app

import android.content.Context
import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao{
    @Upsert
    suspend fun upsertTicket(ticketDetails: TicketDetails)

    @Delete
    suspend fun deleteTicket(ticketDetails: TicketDetails)

    @Query("SELECT * FROM ticket_details WHERE ticketType = 'closing' ORDER BY id ASC")
    fun getClosingTicketsOrderedByID(): Flow<List<TicketDetails>>

    @Query("SELECT * FROM ticket_details WHERE ticketType = 'opening' ORDER BY id ASC")
    fun getOpeningTicketsOrderedByID(): Flow<List<TicketDetails>>

    @Query("UPDATE ticket_details SET order_id = :newOrderIndex WHERE id = :ticketId")
    fun updateOrderIndex(ticketId: Int, newOrderIndex: Int)

    @Query("SELECT * FROM ticket_details WHERE id = :ticketId")
    fun getTicketDetails(ticketId: Int) : TicketDetails
}

sealed interface TicketEvent {
    object SaveTicket: TicketEvent
    data class SetName(val Name: String): TicketEvent
    data class SetPicture(val Picture: Uri, val context: Context): TicketEvent
    data class SetTicketType(val ticketType: String): TicketEvent
    object ShowDialog: TicketEvent
    object HideDialog: TicketEvent
    data class SortTickets(val sortType: String): TicketEvent
    data class DeleteTickets(val ticketDetails: TicketDetails): TicketEvent
    data class GetTicket(val ticketId: Int): TicketEvent
}

data class TicketState(
    val tickets: List<TicketDetails> = emptyList(),
    var ticketType: String = "",
    var Name: String = "",
    var Picture: ByteArray? = null,
    var Time: String = "",
    var Seat: String = "",
    var id: Int = 0
)