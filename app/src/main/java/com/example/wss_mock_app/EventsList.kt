package com.example.wss_mock_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.wss_mock_app.data.EventDetails

@Composable
fun EventsList(
    navController: NavController,
    events: List<EventDetails>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Events List",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(0.dp, 20.dp)
                .fillMaxWidth()
        )
        Text(
            text = "All / Unread / Read",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth()
        )
        EventsCard(events = events, onClick = { event ->
            navController.navigate(EventsScreen.Details.route.replace("{eventId}", event.id))
        })
    }
}

sealed class EventsScreen(val route: String) {
    object EventsList : EventsScreen("events_list")
    object Details : EventsScreen("details/{eventId}")
}

fun findEventById(
    eventId: String?,
    events: List<EventDetails>
): EventDetails? {
    return events.find { it.id == eventId }
}


@Composable
fun DetailsScreen(event: EventDetails) {
    // Display event details here
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Event Details",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(0.dp, 20.dp, 0.dp, 20.dp)
                .fillMaxWidth()
        )
        Text(
            text = event.Name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 5.dp)
                .fillMaxWidth()
        )
        Text(
            text = "30 views",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            var selectedImage = remember { mutableStateOf<Painter?>(null) }
            var isExpanded = remember {
                mutableStateOf(false)
            }
            ImageList(
                images = event.Pictures,
                isExpanded = isExpanded,
                selectedImage = selectedImage
            )

            if (isExpanded.value && selectedImage.value != null) {
                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = { isExpanded.value = false }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(Color.Gray.copy(alpha = 0.8f))
                            .fillMaxSize()
                            .clickable { isExpanded.value = false }
                    ) {
                        Image(
                            painter = selectedImage.value!!,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        Text(
            text = event.DetailedDescription,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(15.dp, 15.dp, 15.dp, 15.dp),
            fontSize = 15.sp
        )
    }
}

@Composable
@FontScalePreview
@PixelCPreview
@Pixel2Preview
fun DetailsScreenPreview() {
    val event =
        EventDetails(
            "1",
            "Android1",
            "This is android 1",
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
        )
    DetailsScreen(event = event)
}

@Composable
fun EventsCard(
    events: List<EventDetails>,
    onClick: (EventDetails) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(events) { event ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(event)
                }) {
                Row {
                    Image(
                        painter = event.Thumbnail,
                        contentDescription = "Picture of ${event.Name}",
                        modifier = Modifier
                            .size(125.dp)
                            .padding(10.dp)
                    )
                    Column {
                        Text(
                            text = event.Name,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 5.dp)
                        )
                        Text(
                            text = event.Description,
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)
                        )
                        val status: String = if (event.Status) {
                            "Read"
                        } else {
                            "Unread"
                        }
                        Text(text = status)
                    }
                }
            }
            Divider()
        }
    }
}


@Composable
fun ImageList(
    images: List<Painter>,
    isExpanded: MutableState<Boolean>,
    selectedImage: MutableState<Painter?>
) {
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