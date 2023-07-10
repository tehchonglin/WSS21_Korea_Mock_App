package com.example.wss_mock_app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class TicketViewModel(
    private val dao: TicketDao
) : ViewModel() {
    private val _sortType = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tickets = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
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

    private val _stateOpening = MutableStateFlow(TicketState())
    private val _stateClosing = MutableStateFlow(TicketState())

    val stateOpening =
        combine(_stateOpening, _sortTypeOpening, _ticketsOpeningSort) { state, sortType, tickets ->
            state.copy(tickets = tickets, ticketType = sortType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    val stateClosing =
        combine(_stateClosing, _sortTypeClosing, _ticketsClosingSort) { state, sortType, tickets ->
            state.copy(tickets = tickets, ticketType = sortType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    private val _ticketDetails = MutableStateFlow(TicketState())

    val currentTicket = combine(_ticketDetails,_state){ ticketDetails, state ->
        state.copy(
            ticketType = ticketDetails.ticketType,
            Name = ticketDetails.Name,
            Seat = ticketDetails.Seat,
            Time = ticketDetails.Time,
            Picture = ticketDetails.Picture,
            id = state.id
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TicketState())

    fun onEvent(event: TicketEvent) {
        when (event) {
            is TicketEvent.DeleteTickets -> {
                viewModelScope.launch {
                    dao.deleteTicket(event.ticketDetails)
                }
            }

            TicketEvent.HideDialog -> TODO()
            TicketEvent.SaveTicket -> {
                val name = _state.value.Name
                val picture = _state.value.Picture
                val type = _state.value.ticketType

                if (name.isBlank() || picture == null || type.isBlank()) {
                    return
                }

                val ticket = TicketDetails(
                    type,
                    name,
                    picture,
                    generateDate(),
                    generateSeat(),
                    0
                )
                viewModelScope.launch {
                    dao.upsertTicket(ticket)
                }
                _state.update {
                    it.copy(
                        Name = "",
                        Picture = null,
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

            TicketEvent.ShowDialog -> TODO()
            is TicketEvent.SortTickets -> {
                if (event.sortType == "opening") {
                    _sortTypeOpening.value = event.sortType
                } else {
                    _sortTypeClosing.value = event.sortType
                }
            }

            is TicketEvent.SetPicture -> {
                viewModelScope.launch {
                    val uri = event.Picture
                    val context = event.context
                    _state.update {
                        it.copy(
                            Picture = uriToByteArray(uri = uri, context)
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
                    val ticketDetails = withContext(Dispatchers.IO) {
                        dao.getTicketDetails(event.ticketId)
                    }
                    withContext(Dispatchers.Main) {
                        currentTicket.value.Name = ticketDetails.Name
                        currentTicket.value.ticketType = ticketDetails.ticketType
                        currentTicket.value.Seat = ticketDetails.Seat
                        currentTicket.value.Time = ticketDetails.Time
                        currentTicket.value.id = ticketDetails.id
                        currentTicket.value.Picture = ticketDetails.Picture
                    }
                }
            }
        }
    }
}


fun uriToByteArray(
    uri: Uri,
    context: Context
): ByteArray {
    var bitmap: Bitmap? = null
    val inputStream = context.contentResolver.openInputStream(uri)
    inputStream?.use {
        bitmap = BitmapFactory.decodeStream(it)
    }
    val stream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

//TODO: Make algorithm to generate seats
fun generateSeat(): String {
    return "A2 SEAT 1"
}

//TODO: Make algorithm to generate date
@SuppressLint("SimpleDateFormat")
fun generateDate(): String {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(time)
}