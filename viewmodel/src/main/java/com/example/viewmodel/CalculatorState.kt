package com.example.viewmodel

data class CalculatorState(
    val getFirstQuery: Int? = 0,
    val getOperation: Operation? = null,
    val getSecondQuery: Int = 0
)