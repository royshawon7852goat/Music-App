package com.pulse.music.data.models

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: String,
    val albumArtUri: String? = null,
    val relativePath: String? = null,
    val size: Long = 0L,
    val dateAdded: Long = 0L,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L
)

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val songCount: Int,
    val albumArtUri: String? = null
)

data class Artist(
    val id: Long,
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)

data class MusicFolder(
    val name: String,
    val path: String,
    val songCount: Int
)

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class SortOrder {
    TITLE_ASC,
    TITLE_DESC,
    ARTIST_ASC,
    ALBUM_ASC,
    DATE_ADDED_DESC,
    DURATION_DESC
}

data class LyricLine(
    val timestampMs: Long,
    val text: String
)
