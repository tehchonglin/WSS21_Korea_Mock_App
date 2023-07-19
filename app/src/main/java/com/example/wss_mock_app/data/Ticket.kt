package com.example.wss_mock_app.data

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

    @Query("SELECT * FROM ticket_details WHERE id = :ticketId")
    fun getTicketDetails(ticketId: Int) : TicketDetails

    @Query("SELECT * FROM audio ORDER BY id ASC")
    fun getAudioFile(): Flow<List<Audio>>

    @Upsert
    suspend fun upsertAudio(audio: Audio)
}

sealed interface TicketEvent {
    object SaveTicket: TicketEvent
    object SaveAudio: TicketEvent
    data class SetFile(val filePath: String): TicketEvent
    data class SetName(val Name: String): TicketEvent
    data class SetPicture(val Picture: String): TicketEvent
    data class SetTicketType(val ticketType: String): TicketEvent
    data class SortTickets(val sortType: String): TicketEvent
    data class DeleteTickets(val ticketDetails: TicketDetails): TicketEvent
    data class GetTicket(val ticketId: Int): TicketEvent
}

data class AudioState(
    val audioDetails: List<Audio> = emptyList(),
    var audioFile: String = "",
    var id: Int = 0
    )

data class TicketState(
    val tickets: List<TicketDetails> = emptyList(),
    var ticketType: String = "",
    var Name: String = "",
    var Picture: String = "",
    var Time: String = "",
    var Seat: String = "",
    var id: Int = 0
)