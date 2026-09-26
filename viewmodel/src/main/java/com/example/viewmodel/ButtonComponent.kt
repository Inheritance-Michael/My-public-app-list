package com.example.viewmodel


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorBTN(
    symbol: String,
    modifier: Modifier,
    onClick: () -> Unit,
    textColor: Color,
){
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable{ onClick() }
            .then(modifier),
        contentAlignment = Alignment.Center
    ){
        Text(
            symbol,
            style = MaterialTheme.typography.displayMedium,
            color = textColor,
        )
    }
}