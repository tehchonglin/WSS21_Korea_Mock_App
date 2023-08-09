package com.example.wss_mock_app.data

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
