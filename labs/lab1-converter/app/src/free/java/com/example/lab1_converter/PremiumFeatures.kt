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
    // For the free version, we just call the content with empty composables
    content(
        swapButton = { /* Empty */ },
        copyFromButton = { /* Empty */ },
        copyToButton = { /* Empty */ }
    )
}
