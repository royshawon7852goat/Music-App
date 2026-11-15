package com.pulse.music.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulse.music.data.models.Playlist
import com.pulse.music.data.models.Song
import com.pulse.music.ui.components.SongItem
import com.pulse.music.ui.theme.*

@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    favoriteSongs: List<Song>,
    recentlyPlayed: List<Song>,
    recentlyAdded: List<Song>,
    mostPlayed: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onCreatePlaylist: (String) -> Unit,
    onRenamePlaylist: (Long, String) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onSongClick: (List<Song>, Int) -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onGetPlaylistSongs: (Long) -> kotlinx.coroutines.flow.Flow<List<Song>>,
    onRemoveSongFromPlaylist: (Long, Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var playlistToRename by remember { mutableStateOf<Playlist?>(null) }
    var selectedPlaylistForDetail by remember { mutableStateOf<Playlist?>(null) }

    // If a playlist is selected, show detail view
    if (selectedPlaylistForDetail != null) {
        val playlist = selectedPlaylistForDetail!!
        val songsFlow = remember(playlist.id) {
            when (playlist.name) {
                "Favorites" -> kotlinx.coroutines.flow.flowOf(favoriteSongs)
                "Recently Played" -> kotlinx.coroutines.flow.flowOf(recentlyPlayed)
                "Recently Added" -> kotlinx.coroutines.flow.flowOf(recentlyAdded)
                "Most Played" -> kotlinx.coroutines.flow.flowOf(mostPlayed)
                else -> onGetPlaylistSongs(playlist.id)
            }
        }
        val playlistSongs by songsFlow.collectAsState(initial = emptyList())

        PlaylistDetailView(
            playlist = playlist,
            songs = playlistSongs,
            currentSong = currentSong,
            isPlaying = isPlaying,
            onBackClick = { selectedPlaylistForDetail = null },
            onPlayAll = { onSongClick(playlistSongs, 0) },
            onShuffleAll = {
                val shuffled = playlistSongs.shuffled()
                onSongClick(shuffled, 0)
            },
            onSongClick = { song -> onSongClick(playlistSongs, playlistSongs.indexOf(song)) },
            onFavoriteToggle = onFavoriteToggle,
            onRemoveSong = { song -> onRemoveSongFromPlaylist(playlist.id, song.id) }
        )
        return
    }

    Scaffold(
        containerColor = PulseBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PulseRedAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 90.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Playlists",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PulseTextPrimary
                        )
                        Text(
                            text = "Curated collections and smart playlists",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseTextSecondary
                        )
                    }
                }
            }

            // Default Smart Playlists Section
            item {
                Text(
                    text = "Smart Playlists",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            item {
                SmartPlaylistRow(
                    title = "Favorites",
                    subtitle = "${favoriteSongs.size} tracks",
                    icon = Icons.Default.Favorite,
                    iconTint = PulseRedAccent,
                    onClick = {
                        selectedPlaylistForDetail = Playlist(0, "Favorites", favoriteSongs.size, isDefault = true)
                    }
                )
            }

            item {
                SmartPlaylistRow(
                    title = "Recently Played",
                    subtitle = "${recentlyPlayed.size} tracks",
                    icon = Icons.Default.History,
                    onClick = {
                        selectedPlaylistForDetail = Playlist(-1, "Recently Played", recentlyPlayed.size, isDefault = true)
                    }
                )
            }

            item {
                SmartPlaylistRow(
                    title = "Recently Added",
                    subtitle = "${recentlyAdded.size} tracks",
                    icon = Icons.Default.NewReleases,
                    onClick = {
                        selectedPlaylistForDetail = Playlist(-2, "Recently Added", recentlyAdded.size, isDefault = true)
                    }
                )
            }

            item {
                SmartPlaylistRow(
                    title = "Most Played",
                    subtitle = "${mostPlayed.size} tracks",
                    icon = Icons.Default.TrendingUp,
                    onClick = {
                        selectedPlaylistForDetail = Playlist(-3, "Most Played", mostPlayed.size, isDefault = true)
                    }
                )
            }

            // User Playlists Section
            val customPlaylists = playlists.filter { !it.isDefault }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "My Playlists",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (customPlaylists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No custom playlists yet.\nTap + to create one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(customPlaylists) { playlist ->
                    UserPlaylistRow(
                        playlist = playlist,
                        onClick = { selectedPlaylistForDetail = playlist },
                        onRename = { playlistToRename = playlist },
                        onDelete = { onDeletePlaylist(playlist.id) }
                    )
                }
            }
        }
    }

    // Create Playlist Dialog
    if (showCreateDialog) {
        var playlistName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = PulseCardDark,
            title = { Text("New Playlist", color = PulseTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PulseTextPrimary,
                        unfocusedTextColor = PulseTextPrimary,
                        focusedBorderColor = PulseRedAccent,
                        unfocusedBorderColor = PulseBorderDark
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            onCreatePlaylist(playlistName)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent)
                ) {
                    Text("Create", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = PulseTextMuted)
                }
            }
        )
    }

    // Rename Playlist Dialog
    if (playlistToRename != null) {
        var newName by remember { mutableStateOf(playlistToRename!!.name) }
        AlertDialog(
            onDismissRequest = { playlistToRename = null },
            containerColor = PulseCardDark,
            title = { Text("Rename Playlist", color = PulseTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PulseTextPrimary,
                        unfocusedTextColor = PulseTextPrimary,
                        focusedBorderColor = PulseRedAccent,
                        unfocusedBorderColor = PulseBorderDark
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onRenamePlaylist(playlistToRename!!.id, newName)
                            playlistToRename = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistToRename = null }) {
                    Text("Cancel", color = PulseTextMuted)
                }
            }
        )
    }
}

@Composable
fun SmartPlaylistRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = PulseTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PulseTextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = PulseTextMuted)
    }
}

@Composable
fun UserPlaylistRow(
    playlist: Playlist,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.QueueMusic, contentDescription = null, tint = PulseTextPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = playlist.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PulseTextPrimary)
            Text(text = "Playlist", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = PulseTextMuted)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(PulseCardDark)
            ) {
                DropdownMenuItem(
                    text = { Text("Rename", color = PulseTextPrimary) },
                    onClick = { showMenu = false; onRename() }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = PulseRedAccent) },
                    onClick = { showMenu = false; onDelete() }
                )
            }
        }
    }
}

@Composable
fun PlaylistDetailView(
    playlist: Playlist,
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit,
    onSongClick: (Song) -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onRemoveSong: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PulseCardDark)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PulseTextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${songs.size} tracks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary
                )
            }
        }

        // Action Buttons: Play All & Shuffle All
        if (songs.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPlayAll,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play All", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShuffleAll,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PulseTextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(PulseBorderDark))
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, tint = PulseTextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle", color = PulseTextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            items(songs) { song ->
                SongItem(
                    song = song,
                    isPlaying = isPlaying && currentSong?.id == song.id,
                    isCurrent = currentSong?.id == song.id,
                    onClick = { onSongClick(song) },
                    onFavoriteToggle = { onFavoriteToggle(song) },
                    onMoreClick = if (!playlist.isDefault) {
                        { onRemoveSong(song) }
                    } else null
                )
            }
        }
    }
}
