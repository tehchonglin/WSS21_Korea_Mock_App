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
import com.example.wss_mock_app.data.EventDetails
import com.example.wss_mock_app.room.loadJSONFromAsset
import com.example.wss_mock_app.viewmodel.EventViewModel
import com.google.gson.Gson

@Composable
fun EventsScreen(
    applicationContext: Context,
    eventViewModel: EventViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
    ) {
        val jsonString = loadJSONFromAsset(applicationContext, "sample.json")
        val gson = Gson()
        val eventList = gson.fromJson(jsonString, EventDetails::class.java)
        val navController = rememberNavController()
        NavHost(navController, startDestination = EventsScreen.EventsList.route) {
            composable(EventsScreen.EventsList.route) {
                EventsList(navController, eventList, eventViewModel)
            }
            composable(
                EventsScreen.Details.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = findEventById(
                    eventId,
                    eventList.EventDetails
                ) // Implement this function to find the event by ID
                if (event != null) {
                    DetailsScreen(event, eventViewModel)
                } else {
                    // Handle the case when the event is not found
                }
            }
        }
    }
}
