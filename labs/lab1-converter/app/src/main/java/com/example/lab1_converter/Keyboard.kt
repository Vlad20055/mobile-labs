package com.example.lab1_converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

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
        // Рассчитываем размер кнопки, чтобы она поместилась как по ширине, так и по высоте
        val buttonSize = min((maxWidth - (spacing * 2)) / 3, (maxHeight - (spacing * 3)) / 4)

        // Центрируем сетку кнопок в доступном пространстве
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing),
                userScrollEnabled = false // Отключаем прокрутку
            ) {
                items(keys) { key ->
                    Button(
                        onClick = { onKeyPress(key) },
                        modifier = Modifier.size(buttonSize), // Применяем рассчитанный размер
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(text = key)
                    }
                }
            }
        }
    }
}
