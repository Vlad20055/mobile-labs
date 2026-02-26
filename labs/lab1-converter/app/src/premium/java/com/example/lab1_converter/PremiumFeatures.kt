package com.example.lab1_converter

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
fun PremiumFeatures(
    modifier: Modifier = Modifier,
    fromValue: String,
    toValue: String,
    onSwap: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    Row(modifier = modifier) {
        IconButton(onClick = onSwap) {
            Icon(Icons.Default.SwapVert, contentDescription = "Swap")
        }
        IconButton(onClick = { clipboardManager.setText(AnnotatedString(fromValue)) }) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy From")
        }
        IconButton(onClick = { clipboardManager.setText(AnnotatedString(toValue)) }) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy To")
        }
    }
}
