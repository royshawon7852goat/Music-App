package com.pulse.music.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.pulse.music.data.models.Album
import com.pulse.music.data.models.Artist
import com.pulse.music.data.models.Playlist
import com.pulse.music.data.models.Song
import com.pulse.music.ui.components.SongItem
import com.pulse.music.ui.theme.*

@Composable
fun SearchScreen(
    allSongs: List<Song>,
    allAlbums: List<Album>,
    allArtists: List<Artist>,
    allPlaylists: List<Playlist>,
    searchHistory: List<String>,
    currentSong: Song?,
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    onSongClick: (List<Song>, Int) -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onSaveSearchQuery: (String) -> Unit,
    onDeleteSearchQuery: (String) -> Unit,
    onClearSearchHistory: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredSongs = remember(query, allSongs) {
        if (query.isBlank()) emptyList()
        else allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true)
        }
    }

    val filteredAlbums = remember(query, allAlbums) {
        if (query.isBlank()) emptyList()
        else allAlbums.filter { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) }
    }

    val filteredArtists = remember(query, allArtists) {
        if (query.isBlank()) emptyList()
        else allArtists.filter { it.name.contains(query, ignoreCase = true) }
    }

    val filteredPlaylists = remember(query, allPlaylists) {
        if (query.isBlank()) emptyList()
        else allPlaylists.filter { it.name.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack)
            .statusBarsPadding()
    ) {
        // Search Input Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
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

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (it.length > 2) {
                        onSaveSearchQuery(it)
                    }
                },
                placeholder = { Text("Search songs, artists, albums...", color = PulseTextMuted) },
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = PulseTextMuted)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PulseTextPrimary,
                    unfocusedTextColor = PulseTextPrimary,
                    focusedBorderColor = PulseRedAccent,
                    unfocusedBorderColor = PulseBorderDark,
                    focusedContainerColor = PulseCardDark,
                    unfocusedContainerColor = PulseCardDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Search History Chips (when query is empty)
        if (query.isBlank() && searchHistory.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextSecondary
                )
                TextButton(onClick = onClearSearchHistory) {
                    Text("Clear All", color = PulseRedAccent)
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchHistory) { historyItem ->
                    InputChip(
                        selected = false,
                        onClick = { query = historyItem },
                        label = { Text(historyItem, color = PulseTextPrimary) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = PulseTextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onDeleteSearchQuery(historyItem) }
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = PulseCardDark
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // Songs results
            if (filteredSongs.isNotEmpty()) {
                item {
                    Text(
                        text = "Songs (${filteredSongs.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(filteredSongs) { song ->
                    SongItem(
                        song = song,
                        isPlaying = isPlaying && currentSong?.id == song.id,
                        isCurrent = currentSong?.id == song.id,
                        onClick = {
                            onSaveSearchQuery(query)
                            onSongClick(filteredSongs, filteredSongs.indexOf(song))
                        },
                        onFavoriteToggle = { onFavoriteToggle(song) }
                    )
                }
            }

            // Artists results
            if (filteredArtists.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Artists (${filteredArtists.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(filteredArtists) { artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val artistSongs = allSongs.filter { it.artist.equals(artist.name, ignoreCase = true) }
                                if (artistSongs.isNotEmpty()) {
                                    onSongClick(artistSongs, 0)
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PulseCardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PulseRedAccent)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(artist.name, style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                            Text("${artist.trackCount} tracks", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                        }
                    }
                }
            }

            // Albums results
            if (filteredAlbums.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Albums (${filteredAlbums.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
                items(filteredAlbums) { album ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val albumSongs = allSongs.filter { it.album.equals(album.title, ignoreCase = true) }
                                if (albumSongs.isNotEmpty()) {
                                    onSongClick(albumSongs, 0)
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PulseCardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Album, contentDescription = null, tint = PulseTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(album.title, style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                            Text("${album.artist} • ${album.songCount} songs", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                        }
                    }
                }
            }

            // Empty state for search
            if (query.isNotBlank() && filteredSongs.isEmpty() && filteredAlbums.isEmpty() && filteredArtists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = PulseTextMuted, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No results for \"$query\"", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                            Text("Check your spelling or try another keyword", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                        }
                    }
                }
            }
        }
    }
}
