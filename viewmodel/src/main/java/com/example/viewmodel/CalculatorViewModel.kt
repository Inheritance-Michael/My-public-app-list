package com.example.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.nio.file.Files.delete

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

   }

   private fun enterOperator(operation: Operation) {
      if(state.getFirstQuery == null){
         state = state.copy(operation)
      }
   }

   private fun enterNumber(number: Int) {}

   private fun enterDecimal() {
      TODO("Not yet implemented")
   }

   private fun performCalculation() {
      TODO("Not yet implemented")
   }
}