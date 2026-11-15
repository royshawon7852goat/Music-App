package com.pulse.music.data.repository

import android.content.Context
import com.pulse.music.data.database.PlaylistEntity
import com.pulse.music.data.database.PlaylistSongCrossRef
import com.pulse.music.data.database.PulseDatabase
import com.pulse.music.data.database.SearchHistoryEntity
import com.pulse.music.data.models.*
import com.pulse.music.utils.AudioScanner
import com.pulse.music.utils.AudioScanner.toEntity
import com.pulse.music.utils.AudioScanner.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    private val db = PulseDatabase.getInstance(context)
    private val songDao = db.songDao()
    private val playlistDao = db.playlistDao()
    private val searchHistoryDao = db.searchHistoryDao()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // All songs from DB mapped to domain models
    val allSongs: Flow<List<Song>> = songDao.getAllSongs().map { list ->
        list.map { it.toModel() }
    }

    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs().map { list ->
        list.map { it.toModel() }
    }

    val recentlyPlayedSongs: Flow<List<Song>> = songDao.getRecentlyPlayedSongs().map { list ->
        list.map { it.toModel() }
    }

    val mostPlayedSongs: Flow<List<Song>> = songDao.getMostPlayedSongs().map { list ->
        list.map { it.toModel() }
    }

    val recentlyAddedSongs: Flow<List<Song>> = songDao.getRecentlyAddedSongs().map { list ->
        list.map { it.toModel() }
    }

    val playlists: Flow<List<Playlist>> = playlistDao.getAllPlaylists().map { list ->
        list.map { entity ->
            Playlist(
                id = entity.playlistId,
                name = entity.name,
                createdAt = entity.createdAt,
                isDefault = entity.isDefault
            )
        }
    }

    val searchHistory: Flow<List<String>> = searchHistoryDao.getRecentQueries()

    suspend fun rescanLibrary(): List<Song> = withContext(Dispatchers.IO) {
        _isScanning.value = true
        try {
            val scannedSongs = AudioScanner.scanDeviceAudio(context)
            if (scannedSongs.isNotEmpty()) {
                // Fetch existing DB songs to preserve favorite status, play counts, etc.
                val existingEntities = songDao.getAllSongs().first().associateBy { it.songId }
                val entitiesToInsert = scannedSongs.map { song ->
                    val existing = existingEntities[song.id]
                    song.toEntity(
                        isFavorite = existing?.isFavorite ?: false,
                        playCount = existing?.playCount ?: 0,
                        lastPlayed = existing?.lastPlayedTimestamp ?: 0L
                    )
                }
                songDao.insertSongs(entitiesToInsert)
            }
            scannedSongs
        } finally {
            _isScanning.value = false
        }
    }

    suspend fun toggleFavorite(song: Song) = withContext(Dispatchers.IO) {
        val newFav = !song.isFavorite
        songDao.setFavorite(song.id, newFav)
    }

    suspend fun recordSongPlayback(songId: Long) = withContext(Dispatchers.IO) {
        songDao.recordSongPlayed(songId, System.currentTimeMillis())
    }

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(PlaylistEntity(name = name.trim()))
    }

    suspend fun renamePlaylist(playlistId: Long, newName: String) = withContext(Dispatchers.IO) {
        val existing = playlistDao.getPlaylistById(playlistId)
        if (existing != null) {
            playlistDao.updatePlaylist(existing.copy(name = newName.trim()))
        }
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylistById(playlistId)
        playlistDao.clearPlaylistSongs(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsForPlaylist(playlistId).map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun addSearchQuery(query: String) = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertQuery(SearchHistoryEntity(query.trim()))
        }
    }

    suspend fun deleteSearchQuery(query: String) = withContext(Dispatchers.IO) {
        searchHistoryDao.deleteQuery(query)
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistoryDao.clearHistory()
    }
}
