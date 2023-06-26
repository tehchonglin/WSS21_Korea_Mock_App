package com.example.wss_mock_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsList(navController: NavController,
    onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Tickets List",
        fontSize = 35.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 40.dp, 0.dp, 30.dp),
        textAlign = TextAlign.Center)
        Button(onClick = {
            navController.navigate("ticketDetails")
        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 0.dp, 0.dp, 40.dp),
            colors = ButtonDefaults.buttonColors(
            containerColor = Color.Yellow,
            contentColor = Color.Black
        ), contentPadding = PaddingValues(10.dp),
        shape = RoundedCornerShape(10.dp)) {
            Spacer(modifier = Modifier.width(40.dp))
            Text(text = "Create a new ticket",
            fontSize = 20.sp,
            textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(40.dp))
        }
        Text(text = "Opening Ceremony Tickets",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            textAlign = TextAlign.Center)
        OpeningTickets(onEvent, ticketState)
        Text(text = "Closing Ceremony Tickets",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            textAlign = TextAlign.Center)
        ClosingTickets(onEvent, ticketState)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tickets Create",
            fontSize = 35.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 40.dp, 0.dp, 70.dp),
            textAlign = TextAlign.Center
        )
        val showMenu = remember { mutableStateOf(false) }
        val selectedTicketType = remember { mutableStateOf("") }
        Box {
            Button(
                onClick = { showMenu.value = !showMenu.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(40.dp, 0.dp, 40.dp, 5.dp),
                shape = RectangleShape
            ) {
                Text("Select ticket ceremony type")
            }
            DropdownMenu(
                expanded = showMenu.value,
                onDismissRequest = { showMenu.value = false }
            ) {
                DropdownMenuItem(text = {
                    Text(text = "Opening Ceremony") },
                    onClick = {
                        selectedTicketType.value = "opening"
                        showMenu.value = false
                    })
                DropdownMenuItem( text = {
                    Text(text = "Closing ceremony")
                    },
                    onClick = {
                    selectedTicketType.value = "closing"
                    showMenu.value = false
                })
            }
        }
        var text by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            label = { Text(text = "Name") },
            placeholder = { Text(text = "Input your name") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(40.dp, 0.dp, 40.dp, 5.dp),
            shape = RectangleShape
        )
        val imagePainter = remember { mutableStateOf<Painter?>(null) }
        Button(
            onClick = { TODO()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Cyan,
                contentColor = Color.Black
            ), contentPadding = PaddingValues(10.dp),
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(40.dp, 0.dp, 40.dp, 5.dp)
        ) {
            Text(text = "Choose one picture")
        }
        if (imagePainter.value != null) {
            Image(
                painter = imagePainter.value!!,
                contentDescription = "User selected image",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Gray)
            )
        }
    }
}


@Composable
fun ColumnScope.OpeningTickets(onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
        items(ticketState.tickets) {tickets ->
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
fun ColumnScope.ClosingTickets(onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(ticketState.tickets) {tickets ->
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
