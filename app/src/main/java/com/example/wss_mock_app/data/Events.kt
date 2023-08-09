package com.example.wss_mock_app.data

import com.example.wss_mock_app.media.Picture


data class Event(
    val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val status: Boolean,
    val thumbnail: String,
    val pictures: List<Picture>
)

data class EventDetails(
    val EventDetails: List<Event>
)



