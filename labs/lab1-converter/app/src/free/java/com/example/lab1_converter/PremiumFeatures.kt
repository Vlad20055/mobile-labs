package com.example.lab1_converter

import androidx.compose.runtime.Composable

@Composable
fun PremiumFeatures(
    fromValue: String,
    toValue: String,
    onSwap: () -> Unit,
    content: @Composable (
        swapButton: @Composable () -> Unit,
        copyFromButton: @Composable () -> Unit,
        copyToButton: @Composable () -> Unit
    ) -> Unit
) {
    // Передаем пустые функции без имен
    content(
        { /* Empty swapButton */ },
        { /* Empty copyFromButton */ },
        { /* Empty copyToButton */ }
    )
}
