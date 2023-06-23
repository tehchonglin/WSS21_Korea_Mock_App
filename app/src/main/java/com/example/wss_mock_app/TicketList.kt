package com.example.wss_mock_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TicketsList(navController: NavController,
    opening_events: List<OpeningTicketDetails>,
    closing_events: List<ClosingTicketDetails>,
    opening_state: OpeningTicketState,
    closing_state: ClosingTicketState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Opening Ceremony Tickets",
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp))
        OpeningTickets(events = opening_events, opening_state = opening_state)
        Text(text = "Closing Ceremony Tickets",
            fontSize = 30.sp,
            modifier = Modifier
            .fillMaxWidth())
        ClosingTickets(events = closing_events, closing_state = closing_state)
    }
}

@Composable
fun OpeningTickets(events: List<OpeningTicketDetails>,
    opening_state: OpeningTicketState){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
        items(opening_state.tickets) {tickets ->
            Row (modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tickets.Name,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold)
                    Text(text = tickets.Seat,
                        fontSize = 15.sp,
                        textAlign = TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun ClosingTickets(events: List<ClosingTicketDetails>,
    closing_state: ClosingTicketState){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(closing_state.tickets) {tickets ->
            Row (modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tickets.Name,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold)
                    Text(text = tickets.Seat,
                        fontSize = 15.sp,
                        textAlign = TextAlign.End)
                }
            }
        }
    }
}