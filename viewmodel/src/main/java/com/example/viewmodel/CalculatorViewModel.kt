package com.example.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel: ViewModel() {
   var state by mutableStateOf(CalculatorState())
      private set

   fun event(onEvent: CalculatorEvent){
      when(onEvent){
          CalculatorEvent.Calculate -> performCalculation()
          CalculatorEvent.Clear -> state = CalculatorState()
          CalculatorEvent.Decimal -> enterDecimal()
          CalculatorEvent.Delete -> performDeletion()
          is CalculatorEvent.Number -> enterNumber(onEvent.number)
          is CalculatorEvent.Operator -> enterOperator(onEvent.operator)
      }
   }

   private fun performDeletion() {
       when{
           state.getSecondQuery.isNotBlank() -> state = state.copy(
               getSecondQuery = state.getSecondQuery.dropLast(1)
           )
           state.getOperation != null -> state = state.copy(
               getOperation = null
           )
           state.getFirstQuery.isNotBlank() -> state = state.copy(
               getFirstQuery = state.getFirstQuery.dropLast(1)
           )
       }
   }

   private fun enterOperator(operation: Operation) {
      if(state.getFirstQuery.isNotBlank()){
         state = state.copy( getOperation = operation)
      }
   }

   private fun enterNumber(number: Int) {
       if(state.getOperation == null){
           if(state.getFirstQuery.length >= MAX_NUM_LENGTH){
               return
           }
           state = state.copy(
               getFirstQuery = state.getFirstQuery + number
           )
           return
       }

       if(state.getSecondQuery.length >= MAX_NUM_LENGTH){
           return
       }else{
           state = state.copy(
               getSecondQuery = state.getSecondQuery + number
           )
       }

   }

    companion object{
        private const val MAX_NUM_LENGTH = 8
    }

   private fun enterDecimal() {
      if(
          state.getOperation == null
          && !state.getFirstQuery.contains(".")
          && !state.getSecondQuery.isNotBlank()
      ){
          state = state.copy(
              getFirstQuery = state.getFirstQuery + "."
          )
          return
      }

       if(
            !state.getSecondQuery.contains(".")
           && !state.getSecondQuery.isNotBlank()
       ){
           state = state.copy(
               getSecondQuery = state.getSecondQuery + "."
           )
       }
   }

   private fun performCalculation() {
      val firstNumber = state.getFirstQuery.toDoubleOrNull()
       val secondNumber = state.getSecondQuery.toDoubleOrNull()

       if(firstNumber != null && secondNumber != null){
           val result = when(state.getOperation){
               Operation.Add -> firstNumber + secondNumber
               Operation.Divide -> firstNumber / secondNumber
               Operation.Multiple -> firstNumber * secondNumber
               Operation.Subtrack -> firstNumber - secondNumber
               null -> return
           }
           state = state.copy(
               getFirstQuery =  result.toString().take(15),
               getOperation = null,
               getSecondQuery = ""
           )
       }
   }
}