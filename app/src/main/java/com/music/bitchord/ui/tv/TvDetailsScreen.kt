package com.music.bitchord.ui.tv

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.DetailPage
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.UiState
import com.music.bitchord.playback.playSongs
import com.music.bitchord.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvDetailsScreen(
    viewModel: MainViewModel,
    controller: MediaController?,
    item: ShelfItem,
    onNavigateBack: () -> Unit,
) {
    val browseId = item.browseId ?: run {
        onNavigateBack()
        return
    }
    LaunchedEffect(browseId) {
        viewModel.openDetail(
            browseId = browseId,
            title = item.title,
            subtitle = item.subtitle,
            thumbnailUrl = item.thumbnailUrl,
        )
    }
    val detailStack by viewModel.detailStack.collectAsState()
    val page: DetailPage? = detailStack.lastOrNull { it.browseId == browseId }
    val scope = rememberCoroutineScope()

    fun playAll(songs: List<Song>, index: Int = 0) {
        val c = controller ?: return
        scope.launch { c.playSongs(songs, index) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(12.dp))
                val songs = (page?.songs as? UiState.Success)?.data
                Button(
                    onClick = { songs?.let { playAll(it) } },
                    enabled = songs?.isNotEmpty() == true,
                ) {
                    Text("Play")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        when (val songsState = page?.songs) {
            null, is UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading…")
            }
            is UiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Couldn't load this page.")
            }
            is UiState.Success -> {
                val songs = songsState.data
                if (songs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No tracks here yet.")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        itemsIndexed(songs) { index, song ->
                            Card(
                                onClick = { playAll(songs, index) },
                                modifier = Modifier.fillMaxWidth(),
                                scale = CardDefaults.scale(focusedScale = 1.02f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.width(40.dp),
                                    )
                                    AsyncImage(
                                        model = song.thumbnailUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
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
                                    song.durationText?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
