package com.example.wss_mock_app.presentation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wss_mock_app.data.AudioState
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.data.TicketState
import com.example.wss_mock_app.viewmodel.EventViewModel


@Composable
fun Navigation(
    navController: NavHostController,
    stateOpening: TicketState,
    stateClosing: TicketState,
    stateDetails: TicketState,
    onEvent: (TicketEvent) -> Unit,
    applicationContext: Context,
    audioState: AudioState,
    eventViewModel: EventViewModel
) {
    NavHost(navController = navController, startDestination = "Events") {
        composable("Events") {
            EventsScreen(applicationContext, eventViewModel)
        }
        composable("Tickets") {
            TicketsScreen(stateOpening, stateClosing, stateDetails, onEvent, applicationContext)
        }
        composable("Records") {
            RecordsScreen(applicationContext, audioState, onEvent)
        }
    }
}
