@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wss_mock_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.wss_mock_app.ui.theme.WSS_Mock_AppTheme

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
        setContent {
            WSS_Mock_AppTheme {
                val stateOpening by viewModel.stateOpening.collectAsState()
                val stateClosing by viewModel.stateClosing.collectAsState()
                val stateDetails by viewModel.currentTicket.collectAsState()
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
                        viewModel::onEvent
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
    onEvent: (TicketEvent) -> Unit
) {
    NavHost(navController = navController, startDestination = "Events") {
        composable("Events") {
            EventsScreen()
        }
        composable("Tickets") {
            TicketsScreen(stateOpening, stateClosing, stateDetails, onEvent)
        }
        composable("Records") {
            RecordsScreen()
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
                        Log.d("Ticket List", "ticketDetails/$it")
                        navController.navigate("ticketDetails/$it")
                    }
                )
            }
            composable(
                route = "ticketDetails/{ticket_id}",
                arguments = listOf(
                navArgument("ticket_id"){
                    type = NavType.IntType
                }
                )){
                val id = it.arguments?.getInt("ticket_id") ?: ""
                Log.d("Path", "ID: $id")
                TicketDetailsScreen(navController, onEvent, stateDetails, id as Int)
            }
            composable("createTicket") {
                CreateTicketScreen(navController, onEvent)
            }
        }
    }
}


@Composable
fun RecordsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Records Screen")
    }
}
