package com.example.wss_mock_app

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wss_mock_app.data.Audio
import com.example.wss_mock_app.data.AudioState
import com.example.wss_mock_app.data.TicketDao
import com.example.wss_mock_app.data.TicketDetails
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.data.TicketState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

class TicketViewModel(
    private val dao: TicketDao
) : ViewModel() {
    private val _state = MutableStateFlow(TicketState())
    private val _ticketState = MutableStateFlow(TicketState())

    // Sort state for tickets opening
    private val _sortTypeOpening = MutableStateFlow("opening")

    // Tickets state for opening sort
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _ticketsOpeningSort = _sortTypeOpening.flatMapLatest { sortType ->
        when (sortType) {
            "opening" -> dao.getOpeningTicketsOrderedByID()
            else -> {
                dao.getOpeningTicketsOrderedByID()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Sort state for tickets closing
    private val _sortTypeClosing = MutableStateFlow("closing")

    // Tickets state for closing sort
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _ticketsClosingSort = _sortTypeClosing.flatMapLatest { sortType ->
        when (sortType) {
            "closing" -> dao.getClosingTicketsOrderedByID()
            else -> {
                dao.getClosingTicketsOrderedByID()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val stateOpening =
        combine(_state, _sortTypeOpening, _ticketsOpeningSort) { state, sortType, tickets ->
            state.copy(tickets = tickets, ticketType = sortType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    val stateClosing =
        combine(_state, _sortTypeClosing, _ticketsClosingSort) { state, sortType, tickets ->
            state.copy(tickets = tickets, ticketType = sortType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    val currentTicket = _ticketState.map {
        it.copy()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    private val _audioState = MutableStateFlow(AudioState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _audioDetails = _audioState.flatMapLatest {
        dao.getAudioFile()
    }
    val audioState = combine(_audioState, _audioDetails) { state, details ->
        state.copy(
            audioDetails = details
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AudioState())


    fun onEvent(event: TicketEvent) {
        when (event) {
            is TicketEvent.DeleteTickets -> {
                viewModelScope.launch {
                    dao.deleteTicket(event.ticketDetails)
                }
            }

            TicketEvent.SaveTicket -> {
                val name = _state.value.Name
                val picture = _state.value.Picture
                val type = _state.value.ticketType

                if (name.isBlank() || picture.isBlank() || type.isBlank()) {
                    return
                }

                val ticket = TicketDetails(
                    type,
                    name,
                    picture,
                    generateDate(),
                    generateSeat()
                )
                viewModelScope.launch {
                    dao.upsertTicket(ticket)
                }
                _state.update {
                    it.copy(
                        Name = "",
                        Picture = "",
                        ticketType = ""
                    )
                }
            }

            is TicketEvent.SetName -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            Name = event.Name
                        )
                    }
                }
            }

            is TicketEvent.SortTickets -> {
                if (event.sortType == "opening") {
                    _sortTypeOpening.value = event.sortType
                } else {
                    _sortTypeClosing.value = event.sortType
                }
            }

            is TicketEvent.SetPicture -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            Picture = event.Picture
                        )
                    }
                }
            }

            is TicketEvent.SetTicketType -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            ticketType = event.ticketType
                        )
                    }
                }
            }

            is TicketEvent.GetTicket -> {
                viewModelScope.launch {
                    val ticket =
                        withContext(Dispatchers.IO) { dao.getTicketDetails(event.ticketId) }
                    _ticketState.update {
                        it.copy(
                            ticketType = ticket.ticketType,
                            Name = ticket.Name,
                            Picture = ticket.Picture,
                            Time = ticket.Time,
                            Seat = ticket.Seat,
                            id = ticket.id
                        )
                    }
                }
            }

            TicketEvent.SaveAudio -> {
                val file = _audioState.value.audioFile
                val audio = Audio(
                    file
                )
                viewModelScope.launch {
                    dao.upsertAudio(audio)
                }
                _audioState.update {
                    it.copy(
                        audioFile = ""
                    )
                }
            }

            is TicketEvent.SetFile -> {
                viewModelScope.launch {
                    _audioState.update {
                        it.copy(
                            audioFile = event.filePath
                        )
                    }
                }
            }
        }
    }
}

//TODO: Make algorithm to generate seats
fun generateSeat(): String {
    return "A2 SEAT 1"
}

@SuppressLint("SimpleDateFormat")
fun generateDate(): String {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(time)
}