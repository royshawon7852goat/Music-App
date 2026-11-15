package com.pulse.music.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pulse.music.data.models.Song
import com.pulse.music.ui.components.MiniPlayer
import com.pulse.music.ui.components.PulseBottomNavigation
import com.pulse.music.ui.equalizer.EqualizerScreen
import com.pulse.music.ui.home.HomeScreen
import com.pulse.music.ui.library.LibraryScreen
import com.pulse.music.ui.player.NowPlayingScreen
import com.pulse.music.ui.playlists.PlaylistsScreen
import com.pulse.music.ui.search.SearchScreen
import com.pulse.music.ui.settings.SettingsScreen
import com.pulse.music.ui.sleeptimer.SleepTimerDialog
import com.pulse.music.ui.theme.*

enum class ActiveScreen {
    HOME,
    LIBRARY,
    PLAYLISTS,
    SETTINGS,
    SEARCH,
    EQUALIZER
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Permission states
    val requiredAudioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, requiredAudioPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasAudioPermission = permissions[requiredAudioPermission] ?: false
        if (hasAudioPermission) {
            viewModel.rescanMusic()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            val perms = mutableListOf(requiredAudioPermission)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionLauncher.launch(perms.toTypedArray())
        } else {
            viewModel.rescanMusic()
        }
    }

    // UI state collections
    val allSongs by viewModel.allSongs.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayedSongs.collectAsState()
    val recentlyAdded by viewModel.recentlyAddedSongs.collectAsState()
    val mostPlayed by viewModel.mostPlayedSongs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // Playback state collections
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val sleepTimerRemainingMs by viewModel.sleepTimerRemainingMs.collectAsState()

    // Equalizer state collections
    val isEqualizerEnabled by viewModel.isEqualizerEnabled.collectAsState()
    val bassBoostStrength by viewModel.bassBoostStrength.collectAsState()
    val equalizerPresets by viewModel.equalizerPresets.collectAsState()
    val currentPreset by viewModel.currentPreset.collectAsState()
    val bandLevels by viewModel.bandLevels.collectAsState()
    val bandFrequencies by viewModel.bandFrequencies.collectAsState()

    // Navigation and overlays
    var currentScreen by remember { mutableStateOf(ActiveScreen.HOME) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    // Playlist picker sheet for adding a song
    var songForPlaylistPicker by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        containerColor = PulseBlack,
        bottomBar = {
            if (!isNowPlayingExpanded && currentScreen != ActiveScreen.SEARCH && currentScreen != ActiveScreen.EQUALIZER) {
                Column {
                    // Mini player floats directly above bottom navigation
                    if (currentSong != null) {
                        MiniPlayer(
                            song = currentSong!!,
                            isPlaying = isPlaying,
                            currentPosition = currentPosition,
                            duration = duration,
                            onPlayPauseClick = { viewModel.playPause() },
                            onNextClick = { viewModel.next() },
                            onExpandClick = { isNowPlayingExpanded = true }
                        )
                    }

                    PulseBottomNavigation(
                        selectedTab = when (currentScreen) {
                            ActiveScreen.HOME -> 0
                            ActiveScreen.LIBRARY -> 1
                            ActiveScreen.PLAYLISTS -> 2
                            ActiveScreen.SETTINGS -> 3
                            else -> 0
                        },
                        onTabSelected = { index ->
                            currentScreen = when (index) {
                                0 -> ActiveScreen.HOME
                                1 -> ActiveScreen.LIBRARY
                                2 -> ActiveScreen.PLAYLISTS
                                3 -> ActiveScreen.SETTINGS
                                else -> ActiveScreen.HOME
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!hasAudioPermission) {
                // Permission Denied / Required Card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(PulseCardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PulseRedAccent, modifier = Modifier.size(36.dp))
                        }
                        Text("Audio Permission Required", style = MaterialTheme.typography.titleLarge, color = PulseTextPrimary)
                        Text(
                            "Pulse needs access to your device's audio files so you can play your local music offline without an internet connection.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = {
                                val perms = mutableListOf(requiredAudioPermission)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                permissionLauncher.launch(perms.toTypedArray())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Grant Permission", color = Color.White)
                        }
                    }
                }
            } else {
                when (currentScreen) {
                    ActiveScreen.HOME -> {
                        HomeScreen(
                            allSongs = allSongs,
                            recentlyPlayed = recentlyPlayed,
                            recentlyAdded = recentlyAdded,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            isScanning = isScanning,
                            onSongClick = { songs, index -> viewModel.playSong(songs, index) },
                            onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                            onQuickAccessClick = { route ->
                                when (route) {
                                    "all_songs" -> currentScreen = ActiveScreen.LIBRARY
                                    "favorites" -> currentScreen = ActiveScreen.PLAYLISTS
                                    "albums" -> currentScreen = ActiveScreen.LIBRARY
                                    "playlists" -> currentScreen = ActiveScreen.PLAYLISTS
                                }
                            },
                            onSearchClick = { currentScreen = ActiveScreen.SEARCH },
                            onSettingsClick = { currentScreen = ActiveScreen.SETTINGS },
                            onRescanClick = { viewModel.rescanMusic() }
                        )
                    }
                    ActiveScreen.LIBRARY -> {
                        LibraryScreen(
                            songs = allSongs,
                            albums = albums,
                            artists = artists,
                            folders = folders,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            isScanning = isScanning,
                            onSongClick = { songs, index -> viewModel.playSong(songs, index) },
                            onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                            onRescanClick = { viewModel.rescanMusic() },
                            onAddToPlaylistClick = { song -> songForPlaylistPicker = song }
                        )
                    }
                    ActiveScreen.PLAYLISTS -> {
                        PlaylistsScreen(
                            playlists = playlists,
                            favoriteSongs = favoriteSongs,
                            recentlyPlayed = recentlyPlayed,
                            recentlyAdded = recentlyAdded,
                            mostPlayed = mostPlayed,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            onCreatePlaylist = { name -> viewModel.createPlaylist(name) },
                            onRenamePlaylist = { id, name -> viewModel.renamePlaylist(id, name) },
                            onDeletePlaylist = { id -> viewModel.deletePlaylist(id) },
                            onSongClick = { songs, index -> viewModel.playSong(songs, index) },
                            onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                            onGetPlaylistSongs = { id -> viewModel.getSongsForPlaylist(id) },
                            onRemoveSongFromPlaylist = { pId, sId -> viewModel.removeSongFromPlaylist(pId, sId) }
                        )
                    }
                    ActiveScreen.SETTINGS -> {
                        SettingsScreen(
                            songCount = allSongs.size,
                            albumCount = albums.size,
                            artistCount = artists.size,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onRescanLibrary = { viewModel.rescanMusic() },
                            onEqualizerClick = { currentScreen = ActiveScreen.EQUALIZER },
                            onSleepTimerClick = { showSleepTimerDialog = true }
                        )
                    }
                    ActiveScreen.SEARCH -> {
                        SearchScreen(
                            allSongs = allSongs,
                            allAlbums = albums,
                            allArtists = artists,
                            allPlaylists = playlists,
                            searchHistory = searchHistory,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            onBackClick = { currentScreen = ActiveScreen.HOME },
                            onSongClick = { songs, index -> viewModel.playSong(songs, index) },
                            onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                            onSaveSearchQuery = { query -> viewModel.addSearchQuery(query) },
                            onDeleteSearchQuery = { query -> viewModel.deleteSearchQuery(query) },
                            onClearSearchHistory = { viewModel.clearSearchHistory() }
                        )
                    }
                    ActiveScreen.EQUALIZER -> {
                        EqualizerScreen(
                            isEnabled = isEqualizerEnabled,
                            presets = equalizerPresets,
                            currentPreset = currentPreset,
                            bassBoostStrength = bassBoostStrength,
                            bandFrequencies = bandFrequencies,
                            bandLevels = bandLevels,
                            onToggleEnabled = { viewModel.setEqualizerEnabled(it) },
                            onSelectPreset = { viewModel.setPreset(it) },
                            onBassBoostChange = { viewModel.setBassBoost(it) },
                            onBandLevelChange = { band, level -> viewModel.setBandLevel(band, level) },
                            onReset = { viewModel.resetEqualizer() },
                            onBackClick = { currentScreen = ActiveScreen.SETTINGS }
                        )
                    }
                }
            }

            // Fullscreen Now Playing Overlay with animated slide-up
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NowPlayingScreen(
                    song = currentSong,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    queue = queue,
                    currentIndex = currentIndex,
                    onBackClick = { isNowPlayingExpanded = false },
                    onPlayPauseClick = { viewModel.playPause() },
                    onNextClick = { viewModel.next() },
                    onPreviousClick = { viewModel.previous() },
                    onSeekTo = { pos -> viewModel.seekTo(pos) },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                    onQueueItemClick = { index -> viewModel.playSong(queue, index) },
                    onEqualizerClick = {
                        isNowPlayingExpanded = false
                        currentScreen = ActiveScreen.EQUALIZER
                    },
                    onSleepTimerClick = { showSleepTimerDialog = true }
                )
            }
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            remainingMs = sleepTimerRemainingMs,
            onStartTimer = { mins -> viewModel.startSleepTimer(mins) },
            onCancelTimer = { viewModel.cancelSleepTimer() },
            onDismiss = { showSleepTimerDialog = false }
        )
    }

    // Add to Playlist Dialog
    if (songForPlaylistPicker != null) {
        val songToAdd = songForPlaylistPicker!!
        val customPlaylists = playlists.filter { !it.isDefault }
        AlertDialog(
            onDismissRequest = { songForPlaylistPicker = null },
            containerColor = PulseCardDark,
            title = { Text("Add to Playlist", color = PulseTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                if (customPlaylists.isEmpty()) {
                    Text("No custom playlists yet. Create one in the Playlists tab.", color = PulseTextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        customPlaylists.forEach { p ->
                            Button(
                                onClick = {
                                    viewModel.addSongToPlaylist(p.id, songToAdd.id)
                                    songForPlaylistPicker = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PulseSecondaryDark)
                            ) {
                                Text(p.name, color = PulseTextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { songForPlaylistPicker = null }) {
                    Text("Cancel", color = PulseTextMuted)
                }
            }
        )
    }
}
