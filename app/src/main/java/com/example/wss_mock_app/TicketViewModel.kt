package com.example.wss_mock_app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TicketViewModel  (
    private val dao: TicketDao
    ): ViewModel(){
    private val _sortType = MutableStateFlow("opening")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tickets = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                "opening" -> dao.getOpeningTicketsOrderedByID()
                "closing" -> dao.getClosingTicketsOrderedByID()
                else -> {
                    dao.getOpeningTicketsOrderedByID()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TicketState())
    val state = combine(_state, _sortType, _tickets) { state, sortType, tickets ->
        state.copy(
            tickets = tickets,
            ticketType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    fun onEvent(event: TicketEvent) {
        when(event){
            is TicketEvent.DeleteTickets -> {
                viewModelScope.launch {
                    dao.deleteTicket(event.ticketDetails)
                }
            }
            TicketEvent.HideDialog -> TODO()
            TicketEvent.SaveTicket -> TODO()
            is TicketEvent.SetName ->{
                viewModelScope.launch {
                    _state.update { it.copy(
                        Name = event.Name
                    ) }
                }
            }
            TicketEvent.ShowDialog -> TODO()
            is TicketEvent.SortTickets -> {
                _sortType.value = event.sortType
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