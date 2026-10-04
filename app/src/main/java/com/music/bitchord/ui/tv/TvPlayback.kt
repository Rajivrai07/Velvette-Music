package com.music.bitchord.ui.tv

import com.music.bitchord.data.innertube.InnertubeParser
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.Song

/** Turns a shelf card that represents a track into a playable [Song]. */
fun ShelfItem.toSong(): Song? = videoId?.let { id ->
    Song(
        videoId = id,
        title = title,
        artist = InnertubeParser.artistFromSubtitle(subtitle),
        thumbnailUrl = thumbnailUrl,
    )
}
