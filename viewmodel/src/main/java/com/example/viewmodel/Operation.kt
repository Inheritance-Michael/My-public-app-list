package com.example.viewmodel

sealed class Operation(val symbol: String) {
    object Add: Operation("+")
    object Subtrack: Operation("-")
    object Divide: Operation("/")
    object Multiple: Operation("*")
}
