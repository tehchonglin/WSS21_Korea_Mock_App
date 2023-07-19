package com.example.wss_mock_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.data.TicketState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TicketsList(
    navController: NavController,
    onEvent: (TicketEvent) -> Unit,
    stateOpening: TicketState,
    stateClosing: TicketState,
    onNavigateToTicketDetails: (String) -> Unit
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
                    navController.navigate("createTicket")
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
        openingTickets(onEvent = onEvent, ticketState = stateOpening, onNavigateToTicketDetails)
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
        closingTickets(onEvent, stateClosing, onNavigateToTicketDetails)
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateTicketScreen(
    navController: NavController,
    onEvent: (TicketEvent) -> Unit,
    applicationContext: Context
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedTicketType by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var ticketTypeString by remember { mutableStateOf("") }
    val context = LocalContext.current
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    )
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
            onResult = { uri ->
                imageUri = uri
            }
        )
        Button(
            onClick = {
                if (multiplePermissionState.allPermissionsGranted) {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    multiplePermissionState.launchMultiplePermissionRequest()
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
                    onEvent(TicketEvent.SetName(userName.text))
                    onEvent(TicketEvent.SetTicketType(selectedTicketType))
                    onEvent(TicketEvent.SetPicture(saveTemporaryImage(imageUri!!, applicationContext)))
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

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.openingTickets(
    onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState,
    onNavigateToTicketDetails: (String) -> Unit
) {
    onEvent(TicketEvent.SortTickets("opening"))
    items(ticketState.tickets) { opening_ticket ->
        val id = opening_ticket.id
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            modifier = Modifier.padding(8.dp),
            onClick = ({
                onNavigateToTicketDetails("$id")
            })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
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
                AsyncImage(
                    model = Uri.parse(opening_ticket.Picture),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .size(128.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.closingTickets(
    onEvent: (TicketEvent) -> Unit,
    ticketState: TicketState,
    onNavigateToTicketDetails: (String) -> Unit
) {
    onEvent(TicketEvent.SortTickets("closing"))
    items(ticketState.tickets) { closing_ticket ->
        val id = closing_ticket.id
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            modifier = Modifier.padding(8.dp),
            onClick = ({
                onNavigateToTicketDetails("$id")
            })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
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
                AsyncImage(
                    model = Uri.parse(closing_ticket.Picture),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .size(128.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun TicketDetailsScreen(
    onEvent: (TicketEvent) -> Unit,
    stateDetails: TicketState,
    ticketId: Int
) {
    val screenshotState = rememberScreenshotState()
    onEvent(TicketEvent.GetTicket(ticketId))
    var type by remember { (mutableStateOf("")) }
    type = if (stateDetails.ticketType == "opening") {
        "Opening Ceremony"
    } else {
        "Closing Ceremony"
    }
    val name = stateDetails.Name
    val time = stateDetails.Time
    val seat = stateDetails.Seat
    val fileName = "$name-$seat-$time"
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null)}
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ticket Details",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(0.dp, 40.dp)
                .fillMaxWidth()
        )
        ScreenshotBox(
            screenshotState = screenshotState,
            modifier = Modifier
                .padding(60.dp, 0.dp, 60.dp, 10.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .background(Color.White),
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black)
            ) {
                AsyncImage(
                    model = Uri.parse(stateDetails.Picture),
                    contentDescription = "Ticket Picture",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp, 0.dp, 0.dp, 20.dp)
                        .fillMaxWidth()
                        .height(175.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Ticket Type: $type",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 5.dp, 0.dp, 0.dp)
                )
                Text(
                    text = "Audience's Name: $name",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 5.dp, 0.dp, 0.dp)
                )
                Text(
                    text = "Time: $time",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 5.dp, 0.dp, 0.dp)
                )
                Text(
                    text = "Seat: $seat",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 5.dp, 0.dp, 70.dp)
                )
            }
        }
        LaunchedEffect(Unit) {
            screenshotState.liveScreenshotFlow
                .onEach { bitmap: ImageBitmap ->
                    imageBitmap = bitmap.asAndroidBitmap()
                }
                .launchIn(this)
        }
        Button(
            onClick = {
                screenshotState.capture()
                saveScreenshot(screenshotState, context, fileName)
            },
            modifier = Modifier
                .padding(50.dp, 0.dp, 50.dp, 10.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black
            ), contentPadding = PaddingValues(10.dp),
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(1.dp, Color.DarkGray)
        ) {
            Spacer(modifier = Modifier.width(40.dp))
            Text(
                text = "Download",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}

@SuppressLint("LogConditional")
fun saveScreenshot(screenshotState: ScreenshotState, context: Context, fileName: String) {
    screenshotState.bitmap?.let { saveImage(it, context, fileName) }
    var message = "Image not saved"
    if (screenshotState.bitmap != null){
        message = "Image saved"
    }
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    Log.d("Saving Ticket", screenshotState.bitmap.toString())
}

private fun saveTemporaryImage(uri: Uri, applicationContext: Context): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    val fileName = "Image_${currentDateTime.format(formatter)}"
    val resolver = applicationContext.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val bitmap: Bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))
    val outputStream: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream?.close()
    return imageUri.toString()
}

private fun saveImage(bitmap: Bitmap, context: Context, fileName: String) {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
    values.put(MediaStore.Images.Media.IS_PENDING, true)
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
    val uri: Uri? =
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        val outputStream = context.contentResolver.openOutputStream(uri)
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        values.put(MediaStore.Images.Media.IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
    }
}


//@Composable
//@FontScalePreview
//@Pixel2Preview
//@PixelCPreview
//fun CreateTicketScreenPreview() {
//    val navController = rememberNavController()
//    CreateTicketScreen(navController = navController, {})
//}


//@Composable
//@FontScalePreview
//@Pixel2Preview
//@PixelCPreview
//fun TicketDetailsScreenPreview() {
//    val ticketDetails = TicketState(
//        ticketType = "opening",
//        Name = "Max",
//        Picture = null,
//        Time = generateDate(),
//        Seat = "A2 Seat 1"
//    )
//    TicketDetailsScreen(
//        onEvent = {},
//        stateDetails = ticketDetails,
//        1
//    )
//}

