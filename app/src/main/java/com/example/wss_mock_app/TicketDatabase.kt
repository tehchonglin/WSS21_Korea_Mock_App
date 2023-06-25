package com.example.wss_mock_app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TicketDetails::class],
    version = 1,
    exportSchema = false
)
abstract class TicketDatabase: RoomDatabase() {
    abstract val dao: TicketDao
}