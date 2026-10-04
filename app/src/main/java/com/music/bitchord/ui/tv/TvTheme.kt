package com.music.bitchord.ui.tv

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

/**
 * Velvette Music theme for Android TV. Dark 10-foot UI matching the
 * phone app's dark palette.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VelvetteTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0D0D0F),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1C1C1E),
            onSurfaceVariant = Color(0xFF8E8E93),
            border = Color(0xFF2C2C2E),
        ),
        content = content,
    )
}
