package com.example.wss_mock_app.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wss_mock_app.data.Audio
import com.example.wss_mock_app.media.Converters
import com.example.wss_mock_app.data.TicketDetails

@Database(
    entities = [TicketDetails::class, Audio::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TicketDatabase: RoomDatabase() {
    abstract val dao: TicketDao
}