package com.example.bookkeeper.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase


@Database(
    entities = [Book::class],
    version = 1
)
abstract class BookRoomDatabase: RoomDatabase() {

    abstract fun bookDao(): BookDAO

    // I have not yet understand why 👇

    companion object {
        @Volatile
        private var INSTANCE: BookRoomDatabase? = null

        fun getDatabase(context: Context): BookRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookRoomDatabase::class.java,
                    "book_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
