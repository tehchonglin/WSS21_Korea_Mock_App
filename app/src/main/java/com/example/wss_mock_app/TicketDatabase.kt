package com.example.wss_mock_app

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TicketDetails::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TicketDatabase: RoomDatabase() {
    abstract val dao: TicketDao
}