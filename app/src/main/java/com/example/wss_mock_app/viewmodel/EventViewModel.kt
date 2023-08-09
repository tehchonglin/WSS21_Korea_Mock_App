package com.example.wss_mock_app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel

class EventViewModel(context: Context) : ViewModel() {

    private val sharedPreferences =
        context.getSharedPreferences("EventClicks", Context.MODE_PRIVATE)

    fun getClickCount(eventId: String): Int {
        return sharedPreferences.getInt(eventId, 0)
    }

    fun incrementClickCount(eventId: String) {
        val clickCount = getClickCount(eventId)
        sharedPreferences.edit().putInt(eventId, clickCount + 1).apply()
    }
}
