package com.example.roomdatabase.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "contact_table")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
