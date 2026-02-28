package com.example.lab1_converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

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
    val clipboardManager = LocalClipboardManager.current

    val swapButton = @Composable {
        IconButton(onClick = onSwap) {
            Icon(Icons.Filled.SwapVert, contentDescription = "Swap")
        }
    }

    val copyFromButton = @Composable {
        IconButton(onClick = { clipboardManager.setText(AnnotatedString(fromValue)) }) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy From")
        }
    }

    val copyToButton = @Composable {
        IconButton(onClick = { clipboardManager.setText(AnnotatedString(toValue)) }) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy To")
        }
    }

    content(swapButton, copyFromButton, copyToButton)
}
