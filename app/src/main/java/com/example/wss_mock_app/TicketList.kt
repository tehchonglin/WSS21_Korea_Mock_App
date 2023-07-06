package com.example.wss_mock_app

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsList(
    navController: NavController,
    onEvent: (TicketEvent) -> Unit,
    stateOpening: TicketState,
    stateClosing: TicketState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Tickets List",
                fontSize = 35.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 40.dp, 0.dp, 30.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {
                    navController.navigate("ticketDetails")
                },
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                ), contentPadding = PaddingValues(10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Create a new ticket",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
            Text(
                text = "Opening Ceremony Tickets",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                textAlign = TextAlign.Center
            )
        }
        openingTickets(onEvent = onEvent, ticketState = stateOpening)
        item {
            Text(
                text = "Closing Ceremony Tickets",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                textAlign = TextAlign.Center
            )
        }
        closingTickets(onEvent, stateClosing)
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateTicketScreen(
    navController: NavController,
    onEvent: (TicketEvent) -> Unit,
    multiplePermissionResultLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedTicketType by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var ticketTypeString by remember { mutableStateOf("") }
    val context = LocalContext.current
    val permissionsToRequest = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO
    )
    val imagePermissionsState =
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    val videoPermissionsState =
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_VIDEO)
    val audioPermissionsState =
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp, 40.dp, 5.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = ticketTypeString,
                onValueChange = { ticketTypeString = it },
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    }
                    .fillMaxWidth(),
                label = { Text("Select ticket ceremony type") },
                trailingIcon = {
                    Icon(icon, "contentDescription",
                        Modifier.clickable { showMenu = !showMenu })
                },
                shape = RectangleShape,
                readOnly = true
            )
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                DropdownMenuItem(text = {
                    Text(text = "Opening Ceremony")
                },
                    onClick = {
                        selectedTicketType = "opening"
                        ticketTypeString = "Opening ceremony"
                        showMenu = false
                    })
                DropdownMenuItem(text = {
                    Text(text = "Closing ceremony")
                },
                    onClick = {
                        selectedTicketType = "closing"
                        ticketTypeString = "Closing ceremony"
                        showMenu = false
                    })
            }
        }
        var userName by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = userName,
            onValueChange = {
                userName = it
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
            onResult = { uri -> imageUri = uri }
        )
        Button(
            onClick = {
                if(audioPermissionsState.status.isGranted and videoPermissionsState.status.isGranted and imagePermissionsState.status.isGranted){
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                }
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
                    .width(330.dp)
                    .height(150.dp)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .width(330.dp)
                    .height(150.dp)
                    .background(Color.Gray)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Button(
            onClick = {
                if (!(imageUri != null && selectedTicketType != "" && userName.text != "")) {
                    Toast.makeText(context, "Please fill in the blanks", Toast.LENGTH_LONG).show()
                } else {
                    Log.d("TicketCreateScreen", selectedTicketType)
                    onEvent(TicketEvent.SetName(userName.text))
                    onEvent(TicketEvent.SetTicketType(selectedTicketType))
                    onEvent(TicketEvent.SetPicture(imageUri!!))
                    onEvent(TicketEvent.SaveTicket)
                    navController.navigate("ticketList")
                }
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
            )
        ) {
            Text(
                text = "Create",
                fontSize = 20.sp
            )
        }
    }
}

fun LazyListScope.openingTickets(
    onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState
) {
    onEvent(TicketEvent.SortTickets("opening"))
    items(ticketState.tickets) { opening_ticket ->
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = opening_ticket.Name,
                        fontSize = 20.sp
                    )
                    Text(
                        text = opening_ticket.Time,
                        fontSize = 12.sp
                    )

                }
                val uri = Uri.parse(opening_ticket.Picture)
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "User selected image",
                    modifier = Modifier
                        .background(Color.LightGray)
                        .align(Alignment.CenterVertically)
                        .padding(10.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

fun LazyListScope.closingTickets(
    onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState
) {
    onEvent(TicketEvent.SortTickets("closing"))
    items(ticketState.tickets) { closing_ticket ->
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = closing_ticket.Name,
                        fontSize = 20.sp
                    )
                    Text(
                        text = closing_ticket.Time,
                        fontSize = 12.sp
                    )

                }
                val uri = Uri.parse(closing_ticket.Picture)
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "User selected image",
                    modifier = Modifier
                        .background(Color.LightGray)
                        .align(Alignment.CenterVertically)
                        .padding(10.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
@FontScalePreview
@DevicePreview
fun CreateTicketScreenPreview() {
    val navController = rememberNavController()
    val permissionlauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {})
    CreateTicketScreen(navController = navController, {}, permissionlauncher)
}

@Composable
@FontScalePreview
@DevicePreview
fun TicketsListScreenPreview() {
    val navController = rememberNavController()
    val ticketState = TicketState()
    TicketsList(navController = navController, onEvent = {}, ticketState, ticketState)
}

