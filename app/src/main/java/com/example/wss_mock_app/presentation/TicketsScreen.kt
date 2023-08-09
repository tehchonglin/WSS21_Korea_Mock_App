package com.example.wss_mock_app.presentation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.data.TicketState

@Composable
fun TicketsScreen(
    stateOpening: TicketState,
    stateClosing: TicketState,
    stateDetails: TicketState,
    onEvent: (TicketEvent) -> Unit,
    applicationContext: Context
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 80.dp),
        contentAlignment = Alignment.Center
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "ticketList") {
            composable("ticketList") {
                TicketsList(
                    navController = navController,
                    onEvent = onEvent,
                    stateOpening,
                    stateClosing,
                    onNavigateToTicketDetails = {
                        navController.navigate("ticketDetails/$it")
                    }
                )
            }
            composable(
                route = "ticketDetails/{ticket_id}",
                arguments = listOf(
                    navArgument("ticket_id") {
                        type = NavType.IntType
                    }
                )) {
                val id = it.arguments?.getInt("ticket_id") ?: ""
                TicketDetailsScreen(onEvent, stateDetails, id as Int)
            }
            composable("createTicket") {
                CreateTicketScreen(navController, onEvent, applicationContext)
            }
        }
    }
}