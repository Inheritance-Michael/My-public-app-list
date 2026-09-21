package com.example.roomdatabase.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [
        Contact::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ContactDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactDAO
}