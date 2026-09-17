package com.example.bookkeeper.data

import androidx.room3.Database
import androidx.room3.RoomDatabase


@Database(entities = [Book::class], version = 1)
abstract class BookRoomDatabase: RoomDatabase()