package com.music.bitchord.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.playback.PlayerState
import com.music.bitchord.playback.rememberMediaController
import com.music.bitchord.playback.rememberPlayerState
import com.music.bitchord.ui.MainViewModel

sealed interface TvDestination {
    data object Browse : TvDestination
    data class Details(val item: ShelfItem) : TvDestination
    data object Player : TvDestination
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvApp(viewModel: MainViewModel) {
    val controller = rememberMediaController()
    val playerState: PlayerState = rememberPlayerState(controller)

    var backStack by remember { mutableStateOf<List<TvDestination>>(listOf(TvDestination.Browse)) }

    fun push(destination: TvDestination) {
        backStack = backStack + destination
    }

    fun pop(): Boolean {
        if (backStack.size <= 1) return false
        val popped = backStack.last()
        if (popped is TvDestination.Details) viewModel.closeDetail()
        backStack = backStack.dropLast(1)
        return true
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    when (val destination = backStack.last()) {
        is TvDestination.Browse -> TvBrowseScreen(
            viewModel = viewModel,
            controller = controller,
            playerState = playerState,
            onOpenDetails = { item -> push(TvDestination.Details(item)) },
            onOpenPlayer = { push(TvDestination.Player) },
        )
        is TvDestination.Details -> TvDetailsScreen(
            viewModel = viewModel,
            controller = controller,
            item = destination.item,
            onNavigateBack = { pop() },
        )
        is TvDestination.Player -> TvPlayerScreen(
            playerState = playerState,
            controller = controller,
            onNavigateBack = { pop() },
        )
    }
}
