package com.example.bookkeeper.data

import androidx.lifecycle.LiveData
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO
 * Data Access Object
 *
 * A DAO (Data Access Object) is the bridge between your Kotlin code and your local database.
 *
 * Think of the database as a restaurant kitchen, and the rest of your app as the dining room. The DAO is the waiter. You don't go into the kitchen to cook the data yourself; you just give the waiter a simple order (a Kotlin function), and the waiter handles the messy database operations (raw SQL queries) for you.
 *
 * In Android development, particularly when using Room, a DAO is simply an interface where you define what you want to do, and the system automatically generates the complex database code behind the scenes
 * */

@Dao
interface BookDAO{
    @Insert
    fun insert(book: Book)

    @Update
    fun update(book: Book)

    @Delete
    fun delete(book: Book)

    //Retrieving data

    @Query("SELECT * FROM books")
    fun allBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE title = :searchString OR authorName = :searchString")
    fun getBookByBookOrAuthor(searchString: String): Flow<List<Book>>
}