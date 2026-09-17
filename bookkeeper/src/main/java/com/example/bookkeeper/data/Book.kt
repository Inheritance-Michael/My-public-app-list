package com.example.bookkeeper.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "books")
class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "author_name")
    val author: String
)