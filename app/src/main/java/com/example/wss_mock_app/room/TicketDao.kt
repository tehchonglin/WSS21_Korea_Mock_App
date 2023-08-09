package com.example.wss_mock_app.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.wss_mock_app.data.Audio
import com.example.wss_mock_app.data.TicketDetails
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
