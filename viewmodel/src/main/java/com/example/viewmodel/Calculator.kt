package com.example.viewmodel


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Calculator(
    state: CalculatorState,
    buttonSpacing: Dp =8.dp,
    modifier: Modifier = Modifier,
    onEvent: (CalculatorEvent) -> Unit
) {
    Box(
        modifier = modifier,
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Text(
                text = buildString {
                    append(state.getFirstQuery.toString())
                    append((state.getOperation ?: ""))
                    append(state.getSecondQuery.toString())
                },
                textAlign = TextAlign.End,
                modifier= Modifier.fillMaxWidth()
                    .padding(vertical = 32.dp),
                fontWeight = FontWeight.Light,
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ){
                CalculatorBTN(
                    "AC",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(2f)
                        .weight(2f),
                    onClick = {
                        onEvent(CalculatorEvent.Clear)
                    }
                )
                CalculatorBTN(
                    "Del",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Delete)
                    }
                )
                CalculatorBTN(
                    "/",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Operator(Operation.Divide))
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ){
                CalculatorBTN(
                    "7",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(7))
                    }
                )
                CalculatorBTN(
                    "8",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(8))
                    }
                )
                CalculatorBTN(
                    "9",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(9))
                    }
                )
                CalculatorBTN(
                    "*",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Operator(Operation.Multiple))
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ){
                CalculatorBTN(
                    "4",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(4))
                    }
                )
                CalculatorBTN(
                    "5",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(5))
                    }
                )
                CalculatorBTN(
                    "6",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(6))
                    }
                )
                CalculatorBTN(
                    "-",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Operator(Operation.Subtrack))
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ){
                CalculatorBTN(
                    "1",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(1))
                    }
                )
                CalculatorBTN(
                    "2",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(2))
                    }
                )
                CalculatorBTN(
                    "3",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(3))
                    }
                )
                CalculatorBTN(
                    "+",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Operator(Operation.Add))
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ){
                CalculatorBTN(
                    "0",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(2f)
                        .weight(2f),
                    onClick = {
                        onEvent(CalculatorEvent.Number(0))
                    }
                )
                CalculatorBTN(
                    ".",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Decimal)
                    }
                )
                CalculatorBTN(
                    "=",
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                        .aspectRatio(1f)
                        .weight(1f),
                    onClick = {
                        onEvent(CalculatorEvent.Calculate)
                    }
                )
            }
        }
    }
}