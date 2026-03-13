package com.example.lab1_converter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

@Suppress("BoxWithConstraintsScope")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Keyboard(onKeyPress: (String) -> Unit, modifier: Modifier = Modifier) {
    val keys = listOf(
        "7", "8", "9",
        "4", "5", "6",
        "1", "2", "3",
        ".", "0", "C"
    )

    BoxWithConstraints(modifier = modifier) {
        val spacing = 8.dp
        val buttonSize = min((maxWidth - (spacing * 2)) / 3, (maxHeight - (spacing * 3)) / 4)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing),
                userScrollEnabled = false
            ) {
                items(keys) { key ->
                    if (key == "C") {
                        Surface(
                            modifier = Modifier
                                .size(buttonSize)
                                .combinedClickable(
                                    onClick = { onKeyPress("C") },
                                    onLongClick = { onKeyPress("AC") }
                                ),
                            shape = RoundedCornerShape(0.dp),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = key)
                            }
                        }
                    } else {
                        Button(
                            onClick = { onKeyPress(key) },
                            modifier = Modifier.size(buttonSize),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Text(text = key)
                        }
                    }
                }
            }
        }
    }
}
