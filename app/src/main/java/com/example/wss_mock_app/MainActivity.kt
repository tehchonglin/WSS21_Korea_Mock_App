@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wss_mock_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            "ticket_details.db"
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
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WSS_Mock_AppTheme {
                val opening_state by viewModel.opening_state.collectAsState()
                val closing_state by viewModel.closing_state.collectAsState()
                val navController = rememberNavController()
                Scaffold (
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
                        ){
                    Navigation(navController = navController, closing_state, opening_state, viewModel::onEvent)
                }
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController,
               closing_state: ClosingTicketState,
               opening_state: OpeningTicketState,
               onEvent: (TicketEvent) -> Unit) {
        NavHost(navController = navController, startDestination = "Events"){
        composable("Events"){
            EventsScreen()
        }
        composable("Tickets"){
            TicketsScreen(closing_state,opening_state,onEvent)
        }
        composable("Records"){
            RecordsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    items : List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = Color.LightGray,
        tonalElevation = 5.dp
    ){
        items.forEach{
            item ->
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
                        if(item.badgeCount > 0){
                            BadgedBox(
                                badge = {
                                    Badge{
                                        Text(text = item.badgeCount.toString())
                                    }
                                }) {
                                Icon(imageVector = item.icon,
                                    contentDescription = item.name)
                            }
                        } else {
                            Icon(imageVector = item.icon,
                                contentDescription = item.name)
                        }
                        if (selected) {
                            Text(text = item.name,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp)
                        }
                    }
                })
        }
    }
}

@Composable
fun EventsScreen() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
        ){
        val eventList = listOf(
            EventDetails("1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground))
            ),
            EventDetails("2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent))
            ),
            EventDetails("1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground))
            ),
            EventDetails("2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent))
            ),
            EventDetails("1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground))
            ),
            EventDetails("2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent))
            ),
            EventDetails("1",
                "Android Thumbnail",
                "This is thumbnail of android.",
                "This is the thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.ic_launcher_foreground),
                listOf(painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    painterResource(id = R.drawable.ic_launcher_foreground))
            ),
            EventDetails("2",
                "Arrow Thumbnail",
                "This is arrow thumbnail of android.",
                "This is the arrow thumbnail of android, which looks very nice," +
                        "I really like this thumbnail, and here are 5 reasons why you should too, 1. waewa" +
                        "2. wadwadwa, 3. eqweqwe, 4. ewqeqwe, 5. ytryrtyrt",
                true,
                painterResource(id = R.drawable.arrow_right_transparent),
                listOf(painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent),
                    painterResource(id = R.drawable.arrow_right_transparent))
            ),
        )
        val navController = rememberNavController()
        NavHost(navController, startDestination = EventsScreen.EventsList.route) {
            composable(EventsScreen.EventsList.route) {
                EventsList(navController,eventList)
            }
            composable(
                EventsScreen.Details.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = findEventById(eventId,eventList) // Implement this function to find the event by ID
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
    closing_state: ClosingTicketState,
    opening_state: OpeningTicketState,
    onEvent: (TicketEvent) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp, 0.dp, 0.dp, 50.dp),
        contentAlignment = Alignment.Center){
        val navController = rememberNavController()
        NavHost(navController, startDestination = TicketsScreen.TicketList.route){
            TODO()
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Opening Ceremony Tickets",
                fontSize = 30.sp)
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
            Spacer(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth())
            Text(text = "Closing Ceremony Tickets",
                fontSize = 30.sp)
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

    }
}


@Composable
fun RecordsScreen() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text(text = "Records Screen")
    }
}

@Composable
fun ImageList(images: List<Painter>, isExpanded: MutableState<Boolean>, selectedImage: MutableState<Painter?>) {
    Row {
        images.forEach { image ->
            Image(
                painter = image,
                contentDescription = "some useful description",
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        isExpanded.value = true
                        selectedImage.value = image
                    }
                    .size(100.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    var name by remember {
        mutableStateOf("")
    }
    var names by remember {
        mutableStateOf(listOf<String>())
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(value = name,
                modifier = Modifier.weight(1f),
                onValueChange = { text ->
                    name = text
                })
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if(name.isNotBlank()){
                    names = names + name
                    name = ""
                }
            }) {
                Text(text = "Add")
            }
        }
        NameList(names = names)
    }
}

@Composable
fun NameList(
    names : List<String>,
    modifier: Modifier = Modifier) {
    LazyColumn(modifier){
        items(names){
                currentName ->
            Text(text = currentName,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth())
            Divider()
        }
    }
}