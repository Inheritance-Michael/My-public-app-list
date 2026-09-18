package com.example.roomdatabase.data

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

    @Query("SELECT * FROM contact ORDER BY firstName ASC")
    fun getContactOrderByFirstName(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY lastName ASC")
    fun getContactOrderByLastName(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY phoneNumber ASC")
    fun getContactOrderByPhoneNumber(): Flow<List<Contact>>
}
