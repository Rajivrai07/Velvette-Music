package com.music.bitchord.ui.tv

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.music.bitchord.R
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.UiState
import com.music.bitchord.playback.PlayerState
import com.music.bitchord.playback.playSongs
import com.music.bitchord.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvBrowseScreen(
    viewModel: MainViewModel,
    controller: MediaController?,
    playerState: PlayerState,
    onOpenDetails: (ShelfItem) -> Unit,
    onOpenPlayer: () -> Unit,
) {
    val homeState by viewModel.home.collectAsState()
    val scope = rememberCoroutineScope()
    val firstCardFocus = remember { FocusRequester() }

    LaunchedEffect(homeState) {
        if (homeState is UiState.Success) firstCardFocus.requestFocus()
    }

    fun onItemClick(item: ShelfItem) {
        val song = item.toSong()
        if (song != null) {
            val c = controller ?: return
            scope.launch { c.playSongs(listOf(song), 0) }
        } else if (item.browseId != null) {
            onOpenDetails(item)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Velvette Music",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (val state = homeState) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Loading…", style = MaterialTheme.typography.titleMedium)
                }
                is UiState.Error -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Couldn't load. Check your connection.")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadHome() }) {
                        Text("Retry")
                    }
                }
                is UiState.Success -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    itemsIndexed(state.data) { shelfIndex, shelf ->
                        TvShelf(
                            shelf = shelf,
                            firstCardFocus = if (shelfIndex == 0) firstCardFocus else null,
                            onItemClick = ::onItemClick,
                        )
                    }
                }
            }
        }

        // Now-playing strip
        val song = playerState.song
        if (song != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                onClick = onOpenPlayer,
                modifier = Modifier.fillMaxWidth(),
                scale = CardDefaults.scale(focusedScale = 1.02f),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = song.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = if (playerState.isPlaying) "Playing" else "Paused",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvShelf(
    shelf: HomeShelf,
    firstCardFocus: FocusRequester?,
    onItemClick: (ShelfItem) -> Unit,
) {
    Column {
        Text(
            text = shelf.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            itemsIndexed(shelf.items) { index, item ->
                TvShelfCard(
                    item = item,
                    modifier = if (index == 0 && firstCardFocus != null) {
                        Modifier.focusRequester(firstCardFocus)
                    } else {
                        Modifier
                    },
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvShelfCard(
    item: ShelfItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    StandardCardContainer(
        modifier = modifier.width(160.dp),
        imageCard = { interactionSource ->
            Card(
                onClick = onClick,
                interactionSource = interactionSource,
                scale = CardDefaults.scale(focusedScale = 1.1f),
            ) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp),
                )
            }
        },
        title = {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            Text(
                text = item.subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}
