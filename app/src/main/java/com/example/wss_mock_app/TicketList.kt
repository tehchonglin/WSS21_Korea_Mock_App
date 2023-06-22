package com.example.wss_mock_app

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TicketsList(navController: NavController,
    opening_events: List<OpeningTicketDetails>) {

}

@Composable
fun OpeningTickets() {

}

@Composable
fun ClosingTickets() {

}

sealed class TicketsScreen(val route: String) {
    object TicketList : TicketsScreen("tickets_list")
    object Details : TicketsScreen("details/{ticketId}")
}