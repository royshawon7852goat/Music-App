package com.pulse.music.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pulse.music.PulseApplication
import com.pulse.music.data.models.*
import com.pulse.music.data.repository.MusicRepository
import com.pulse.music.playback.MusicPlayerManager
import com.pulse.music.utils.AudioScanner
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MusicRepository = (application as PulseApplication).musicRepository
    private val playerManager: MusicPlayerManager = (application as PulseApplication).musicPlayerManager

    val allSongs: StateFlow<List<Song>> = repository.allSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentlyPlayedSongs: StateFlow<List<Song>> = repository.recentlyPlayedSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentlyAddedSongs: StateFlow<List<Song>> = repository.recentlyAddedSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mostPlayedSongs: StateFlow<List<Song>> = repository.mostPlayedSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playlists: StateFlow<List<Playlist>> = repository.playlists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchHistory: StateFlow<List<String>> = repository.searchHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isScanning: StateFlow<Boolean> = repository.isScanning

    // Derived Albums, Artists, Folders
    val albums: StateFlow<List<Album>> = allSongs.map { AudioScanner.groupIntoAlbums(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val artists: StateFlow<List<Artist>> = allSongs.map { AudioScanner.groupIntoArtists(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val folders: StateFlow<List<MusicFolder>> = allSongs.map { AudioScanner.groupIntoFolders(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Playback state flows
    val currentSong: StateFlow<Song?> = playerManager.currentSong
    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPosition: StateFlow<Long> = playerManager.currentPosition
    val duration: StateFlow<Long> = playerManager.duration
    val isShuffleEnabled: StateFlow<Boolean> = playerManager.isShuffleEnabled
    val repeatMode: StateFlow<RepeatMode> = playerManager.repeatMode
    val queue: StateFlow<List<Song>> = playerManager.queue
    val currentIndex: StateFlow<Int> = playerManager.currentIndex
    val sleepTimerRemainingMs: StateFlow<Long?> = playerManager.sleepTimerRemainingMs

    // Equalizer state flows
    val isEqualizerEnabled: StateFlow<Boolean> = playerManager.isEqualizerEnabled
    val bassBoostStrength: StateFlow<Int> = playerManager.bassBoostStrength
    val equalizerPresets: StateFlow<List<String>> = playerManager.equalizerPresets
    val currentPreset: StateFlow<Int> = playerManager.currentPreset
    val bandLevels: StateFlow<Map<Int, Int>> = playerManager.bandLevels
    val bandFrequencies: StateFlow<Map<Int, Int>> = playerManager.bandFrequencies

    init {
        // Observe current song changes to record play count and timestamp
        viewModelScope.launch {
            currentSong.collect { song ->
                if (song != null) {
                    repository.recordSongPlayback(song.id)
                }
            }
        }
    }

    fun playSong(songs: List<Song>, startIndex: Int = 0) {
        playerManager.playSong(songs, startIndex)
    }

    fun playPause() {
        playerManager.playPause()
    }

    fun next() {
        playerManager.next()
    }

    fun previous() {
        playerManager.previous()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playerManager.toggleShuffle()
    }

    fun toggleRepeat() {
        playerManager.toggleRepeat()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }

    fun startSleepTimer(minutes: Int) {
        playerManager.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        playerManager.cancelSleepTimer()
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        playerManager.setEqualizerEnabled(enabled)
    }

    fun setPreset(index: Int) {
        playerManager.setPreset(index)
    }

    fun setBassBoost(strength: Int) {
        playerManager.setBassBoost(strength)
    }

    fun setBandLevel(band: Int, level: Int) {
        playerManager.setBandLevel(band, level)
    }

    fun resetEqualizer() {
        playerManager.resetEqualizer()
    }

    fun rescanMusic() {
        viewModelScope.launch {
            repository.rescanLibrary()
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            repository.renamePlaylist(playlistId, newName)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return repository.getSongsForPlaylist(playlistId)
    }

    fun addSearchQuery(query: String) {
        viewModelScope.launch {
            repository.addSearchQuery(query)
        }
    }

    fun deleteSearchQuery(query: String) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }
}
