package com.example.wss_mock_app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [OpeningTicketDetails::class,ClosingTicketDetails::class],
    version = 1
)
abstract class TicketDatabase: RoomDatabase() {
    abstract val dao: TicketDao
}