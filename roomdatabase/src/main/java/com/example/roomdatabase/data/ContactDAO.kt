package com.example.roomdatabase.data

import android.icu.text.MessagePattern
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactDAO {

    @Upsert
    suspend fun upsertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY email ASC")
    fun getContactOrderByEmail(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY company ASC")
    fun getContactOrderByCompany(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY phoneNumber ASC")
    fun getContactOrderByPhoneNumber(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY LOWER(firstName) ASC, LOWER(lastName) ASC")
    fun getAllContact(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE isFavourite = 1")
    fun getContactFavourite(): Flow<List<Contact>>

    @Query( """SELECT * FROM contact WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%' OR phoneNumber Like '%' || :searchQuery || '%' ORDER BY LOWER(firstName) ASC, LOWER(lastName) ASC""")
    fun searchContact(searchQuery: String): Flow<List<Contact>>
}
