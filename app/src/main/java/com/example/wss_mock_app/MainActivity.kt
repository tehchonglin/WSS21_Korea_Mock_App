@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wss_mock_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.wss_mock_app.audio.AndroidAudioPlayer
import com.example.wss_mock_app.audio.AndroidAudioRecorder
import com.example.wss_mock_app.data.AudioState
import com.example.wss_mock_app.data.EventDetails
import com.example.wss_mock_app.data.TicketDatabase
import com.example.wss_mock_app.data.TicketEvent
import com.example.wss_mock_app.data.TicketState
import com.example.wss_mock_app.ui.theme.WSS_Mock_AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TicketDatabase::class.java,
            "tickets.db"
        ).build()
    }
    private val viewModel by viewModels<TicketViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TicketViewModel(db.dao) as T
                }
            }
        }
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.RECORD_AUDIO
            ),
            0
        )
        setContent {
            WSS_Mock_AppTheme {
                val stateOpening by viewModel.stateOpening.collectAsState()
                val stateClosing by viewModel.stateClosing.collectAsState()
                val stateDetails by viewModel.currentTicket.collectAsState()
                val audioState by viewModel.audioState.collectAsState()
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(items = listOf(
                            BottomNavItem(
                                name = "Events",
                                route = "Events",
                                icon = Icons.Default.Event
                            ),
                            BottomNavItem(
                                name = "Tickets",
                                route = "Tickets",
                                icon = Icons.Default.EventSeat
                            ),
                            BottomNavItem(
                                name = "Records",
                                route = "Records",
                                icon = Icons.Default.LibraryMusic
                            )
                        ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route)
                            })
                    }
                ) {
                    Navigation(
                        navController = navController,
                        stateOpening,
                        stateClosing,
                        stateDetails,
                        viewModel::onEvent,
                        applicationContext,
                        audioState
                    )
                }
            }
        }
    }
}

@Composable
fun Navigation(
    navController: NavHostController,
    stateOpening: TicketState,
    stateClosing: TicketState,
    stateDetails: TicketState,
    onEvent: (TicketEvent) -> Unit,
    applicationContext: Context,
    audioState: AudioState
) {
    NavHost(navController = navController, startDestination = "Events") {
        composable("Events") {
            EventsScreen()
        }
        composable("Tickets") {
            TicketsScreen(stateOpening, stateClosing, stateDetails, onEvent)
        }
        composable("Records") {
            RecordsScreen(applicationContext, audioState, onEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = Color.LightGray,
        tonalElevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.White
                ),
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        if (item.badgeCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        }
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                })
        }
    }
}

@Composable
fun EventsScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
    ) {
        val eventList = listOf(
            EventDetails(
                "1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground)
                )
            ),
            EventDetails(
                "2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent)
                )
            ),
            EventDetails(
                "1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground)
                )
            ),
            EventDetails(
                "2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent)
                )
            ),
            EventDetails(
                "1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground)
                )
            ),
            EventDetails(
                "2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent)
                )
            ),
            EventDetails(
                "1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground)
                )
            ),
            EventDetails(
                "2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent)
                )
            ),
        )
        val navController = rememberNavController()
        NavHost(navController, startDestination = EventsScreen.EventsList.route) {
            composable(EventsScreen.EventsList.route) {
                EventsList(navController, eventList)
            }
            composable(
                EventsScreen.Details.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = findEventById(
                    eventId,
                    eventList
                ) // Implement this function to find the event by ID
                if (event != null) {
                    DetailsScreen(event)
                } else {
                    // Handle the case when the event is not found
                }
            }
        }
    }
}


@Composable
fun TicketsScreen(
    stateOpening: TicketState,
    stateClosing: TicketState,
    stateDetails: TicketState,
    onEvent: (TicketEvent) -> Unit
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
                CreateTicketScreen(navController, onEvent)
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordsScreen(applicationContext: Context, audioState: AudioState, onEvent: (TicketEvent) -> Unit) {
    var recordingState by remember { mutableStateOf("stopped") }
    var playingState by remember { mutableStateOf("stopped")}
    var filePath by remember { mutableStateOf("")}
    val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }
    val player by lazy {
        AndroidAudioPlayer(applicationContext){
            playingState = "stopped"
        }
    }
    var audioFile: File? = null
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Records",
                fontSize = 35.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 40.dp, 0.dp, 30.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (permissionState.status.isGranted) {
                                    recordingState = if (recordingState == "stopped") {
                                        File(applicationContext.cacheDir, "audio.mp3").also {
                                            recorder.start(it)
                                            audioFile = it
                                        }
                                        "recording"
                                    } else {
                                        recorder.stop()
                                        Log.d("Audio Status", audioFile.toString())
                                        "stopped"
                                    }
                                } else {
                                    permissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            shape = RectangleShape
                        ) {
                            val text =
                                if (recordingState == "stopped") "Voice Recording" else "Stop Recording"
                            Text(text = text)
                        }
                        Button(
                            onClick = {
                                if(playingState == "stopped") {
                                    player.playFile(audioFile ?: return@Button)
                                    playingState = "playing"
                                } else {
                                    player.stop()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            shape = RectangleShape
                        ) {
                            val text =
                                if (playingState == "stopped") "Voice Play" else "Stop playing"
                            Text(text = text)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.size(0.dp, 100.dp))
                        Button(
                            onClick = {
                                audioFile?.let {
                                    filePath = saveTemporaryFile(it, applicationContext) }
                                onEvent(TicketEvent.SetFile(filePath))
                                onEvent(TicketEvent.SaveAudio)
                                      },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "Submit",
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 30.dp),
                shape = RectangleShape
            ) {
                Text(
                    text = "Audios List",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = CenterHorizontally
                ) {
                    items(audioState.audioDetails) {state ->
                        Card(modifier = Modifier.padding(5.dp, 10.dp)) {
                            var buttonIcon by remember{ mutableStateOf(R.drawable.baseline_play_arrow_24)}
                            Row {
                                Text(
                                    text = "Audio ${state.id}",
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
                                )
                                Button(onClick = {
                                    if(playingState == "stopped"){
                                        val file = File(state.audio)
                                        player.playFile(file)
                                        playingState = "playing"
                                    } else {
                                        player.stop()
                                    }
                                }) {
                                    buttonIcon = if (playingState == "stopped") R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                                    Icon(
                                        painterResource(id = buttonIcon),
                                        contentDescription = null
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

fun saveTemporaryFile(file: File, applicationContext: Context): String{
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    val fileName = "File_${currentDateTime.format(formatter)}"
    val byteArray = file.readBytes()
    val resolver = applicationContext.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
    }
    val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
    resolver.openOutputStream(uri!!)?.use { it.write(byteArray) }
    return uri.toString()
}

@Composable
@Pixel2Preview
//@PixelCPreview
@FontScalePreview
fun RecordScreenPreview() {
    val audioState = AudioState()
    audioState.id = 5
    RecordsScreen(LocalContext.current, audioState, {})
}
