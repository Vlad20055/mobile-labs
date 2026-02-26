package com.example.lab1_converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Keyboard(onKeyPress: (String) -> Unit) {
    val keys = listOf(
        "7", "8", "9",
        "4", "5", "6",
        "1", "2", "3",
        ".", "0", "C"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(keys) { key ->
            Button(
                onClick = { onKeyPress(key) },
                modifier = Modifier.aspectRatio(1f)
            ) {
                Text(text = key)
            }
        }
    }
}
