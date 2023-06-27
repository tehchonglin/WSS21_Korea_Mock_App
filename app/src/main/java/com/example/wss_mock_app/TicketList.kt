package com.example.wss_mock_app

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import org.jetbrains.annotations.Async

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
    var showMenu by remember { mutableStateOf(false) }
    var selectedTicketType by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        Text(
            text = "Tickets Create",
            fontSize = 35.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp, 0.dp, 35.dp),
            textAlign = TextAlign.Center
        )
        // Up Icon when expanded and down icon when collapsed
        val icon = if (showMenu)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown
        val ticketTypeString = if (selectedTicketType.equals("opening"))
            "Opening Ceremony"
        else
            "Closing Ceremony"
        OutlinedTextField(
            value = selectedTicketType,
            onValueChange = { selectedTicketType = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                }
                .padding(40.dp, 0.dp, 40.dp, 5.dp),
            label = {Text("Select ticket ceremony type")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { showMenu = !showMenu })
            },
            shape = RectangleShape
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()}, )
        ) {
            DropdownMenuItem(text = {
                Text(text = "Opening Ceremony") },
                onClick = {
                    selectedTicketType = "opening"
                    showMenu = false
                })
            DropdownMenuItem( text = {
                Text(text = "Closing ceremony")
                },
                onClick = {
                selectedTicketType = "closing"
                showMenu = false
            })
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
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {uri -> imageUri = uri}
        )
        Button(
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
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
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "User selected image",
                modifier = Modifier
                    .size(310.dp)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(325.dp)
                    .background(Color.Gray)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Button(onClick = {
                         TODO( "add the data into the database")
        },
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(40.dp, 50.dp, 40.dp, 5.dp)
                .size(310.dp, 60.dp)
                .border(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Yellow,
            contentColor = Color.Black
        )) {
            Text(text = "Create",
            fontSize = 20.sp)
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

@Composable
@FontScalePreview
@DevicePreview
fun CreateTicketScreenPreview() {
    val navController = rememberNavController()
    CreateTicketScreen(navController = navController)
}
