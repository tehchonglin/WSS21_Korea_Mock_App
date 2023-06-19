package com.example.wss_mock_app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TicketViewModel (
    private val dao: TicketDao
    ): ViewModel(){

    val opening_state = MutableStateFlow(OpeningTicketState())
    val closing_state = MutableStateFlow(ClosingTicketState())

    fun onEvent(event: TicketEvent) {
        when(event){
            is TicketEvent.DeleteOpeningTickets -> {
                viewModelScope.launch {
                    dao.deleteOpeningTicket(event.ticketDetails)
                }
            }
            is TicketEvent.DeleteClosingTickets -> {
                viewModelScope.launch {
                    dao.deleteClosingTicket((event.ticketDetails))
                }
            }
            TicketEvent.SaveOpeningTicket -> {
                val name = opening_state.value.Name
                val picture = opening_state.value.Picture
                val seat = generateSeat()
                val date = generateDate()
                if (name.isBlank() || picture==null){
                    return
                }

                val ticket = OpeningTicketDetails(
                    Name = name,
                    Picture = picture,
                    Date = date,
                    Seat = seat
                )
            }
            TicketEvent.SaveClosingTicket -> {
                val name = closing_state.value.Name
                val picture = closing_state.value.Picture
                val seat = generateSeat()
                val date = generateDate()
                if (name.isBlank() || picture==null){
                    return
                }

                val ticket = ClosingTicketDetails(
                    Name = name,
                    Picture = picture,
                    Date = date,
                    Seat = seat
                )
            }
            is TicketEvent.SetName -> TODO()
            is TicketEvent.SetSeat -> TODO()
            is TicketEvent.SortClosingTickets -> {
                dao.getClosingTicketsOrderedByID().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
            }
            is TicketEvent.SortOpeningTickets -> {
                dao.getOpeningTicketsOrderedByID().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
            }
        }
    }
}

//TODO: Make algorithm to generate seats
fun generateSeat(): String {
    return "A2 SEAT 1"
}

//TODO: Make algorithm to generate date
@SuppressLint("SimpleDateFormat")
fun generateDate(): String{
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(time)
}