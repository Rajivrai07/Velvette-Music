package com.music.bitchord.ui.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.music.bitchord.ui.MainViewModel

/**
 * Leanback launcher entry point for Android TV. Shares the same
 * [MainViewModel] data layer and the same [com.music.bitchord.playback.PlaybackService]
 * as the phone UI — only the presentation is TV-specific.
 */
class TvMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application),
        )[MainViewModel::class.java]
        setContent {
            VelvetteTvTheme {
                TvApp(viewModel = viewModel)
            }
        }
    }
}
