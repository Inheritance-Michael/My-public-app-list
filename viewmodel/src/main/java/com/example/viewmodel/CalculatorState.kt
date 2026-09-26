package com.example.viewmodel

data class CalculatorState(
    val getFirstQuery: String = "",
    val getOperation: Operation? = null,
    val getSecondQuery: String = "",
)