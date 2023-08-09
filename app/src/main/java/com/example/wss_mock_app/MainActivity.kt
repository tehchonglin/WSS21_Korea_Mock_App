@file:OptIn(ExperimentalMaterial3Api::class)
//@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.wss_mock_app


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.wss_mock_app.data.BottomNavItem
import com.example.wss_mock_app.presentation.BottomNavigationBar
import com.example.wss_mock_app.presentation.Navigation
import com.example.wss_mock_app.room.TicketDatabase
import com.example.wss_mock_app.ui.theme.WSS_Mock_AppTheme
import com.example.wss_mock_app.viewmodel.EventViewModel
import com.example.wss_mock_app.viewmodel.TicketViewModel

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

    class EventViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                return EventViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

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
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
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
                val factory = EventViewModelFactory(applicationContext)
                val eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]
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
                        audioState,
                        eventViewModel
                    )
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { owner, event ->
                        if (event == Lifecycle.Event.ON_RESUME && owner == lifecycleOwner) {
                            when (intent.action) {
                                "events" -> navController.navigate("Events")
                                "tickets" -> navController.navigate("Tickets")
                                "records" -> navController.navigate("Records")
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                }
            }
        }
    }
}










