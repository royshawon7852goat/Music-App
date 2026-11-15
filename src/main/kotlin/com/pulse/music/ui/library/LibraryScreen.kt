package com.pulse.music.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pulse.music.data.models.*
import com.pulse.music.ui.components.SongItem
import com.pulse.music.ui.components.formatDuration
import com.pulse.music.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<MusicFolder>,
    currentSong: Song?,
    isPlaying: Boolean,
    isScanning: Boolean,
    onSongClick: (List<Song>, Int) -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onRescanClick: () -> Unit,
    onAddToPlaylistClick: (Song) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Folders")
    var sortOrder by remember { mutableStateOf(SortOrder.TITLE_ASC) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedSongForDetails by remember { mutableStateOf<Song?>(null) }
    var selectedAlbumForView by remember { mutableStateOf<Album?>(null) }
    var selectedArtistForView by remember { mutableStateOf<Artist?>(null) }
    var selectedFolderForView by remember { mutableStateOf<MusicFolder?>(null) }

    // Sorted songs
    val sortedSongs = remember(songs, sortOrder) {
        when (sortOrder) {
            SortOrder.TITLE_ASC -> songs.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> songs.sortedByDescending { it.title.lowercase() }
            SortOrder.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
            SortOrder.ALBUM_ASC -> songs.sortedBy { it.album.lowercase() }
            SortOrder.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
            SortOrder.DURATION_DESC -> songs.sortedByDescending { it.duration }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack)
            .statusBarsPadding()
    ) {
        // Library Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PulseTextPrimary
                )
                Text(
                    text = "${songs.size} songs • ${albums.size} albums",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Rescan button
                IconButton(
                    onClick = onRescanClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PulseCardDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan",
                        tint = if (isScanning) PulseRedAccent else PulseTextPrimary
                    )
                }

                // Sort button
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PulseCardDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = PulseTextPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(PulseCardDark)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Title (A-Z)", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.TITLE_ASC; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Title (Z-A)", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.TITLE_DESC; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Artist", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.ARTIST_ASC; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Album", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.ALBUM_ASC; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Recently Added", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.DATE_ADDED_DESC; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Duration", color = PulseTextPrimary) },
                            onClick = { sortOrder = SortOrder.DURATION_DESC; showSortMenu = false }
                        )
                    }
                }
            }
        }

        // Tabs Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = PulseBlack,
            contentColor = PulseRedAccent,
            edgePadding = 20.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PulseRedAccent
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        selectedAlbumForView = null
                        selectedArtistForView = null
                        selectedFolderForView = null
                    },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) PulseRedAccent else PulseTextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    // All Songs List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 140.dp)
                    ) {
                        items(sortedSongs) { song ->
                            SongItem(
                                song = song,
                                isPlaying = isPlaying && currentSong?.id == song.id,
                                isCurrent = currentSong?.id == song.id,
                                onClick = { onSongClick(sortedSongs, sortedSongs.indexOf(song)) },
                                onFavoriteToggle = { onFavoriteToggle(song) },
                                onMoreClick = { selectedSongForDetails = song }
                            )
                        }
                    }
                }
                1 -> {
                    // Albums Grid or Album Detail
                    if (selectedAlbumForView == null) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 140.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(albums) { album ->
                                AlbumGridItem(album = album, onClick = { selectedAlbumForView = album })
                            }
                        }
                    } else {
                        // Album details list
                        val albumSongs = songs.filter { it.album.equals(selectedAlbumForView?.title, ignoreCase = true) }
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { selectedAlbumForView = null }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PulseTextPrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedAlbumForView!!.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = PulseTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${selectedAlbumForView!!.artist} • ${albumSongs.size} tracks",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PulseTextSecondary
                                    )
                                }
                                Button(
                                    onClick = { onSongClick(albumSongs, 0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Play All", color = Color.White)
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 140.dp)
                            ) {
                                items(albumSongs) { song ->
                                    SongItem(
                                        song = song,
                                        isPlaying = isPlaying && currentSong?.id == song.id,
                                        isCurrent = currentSong?.id == song.id,
                                        onClick = { onSongClick(albumSongs, albumSongs.indexOf(song)) },
                                        onFavoriteToggle = { onFavoriteToggle(song) },
                                        onMoreClick = { selectedSongForDetails = song }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Artists List or Artist Detail
                    if (selectedArtistForView == null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 140.dp)
                        ) {
                            items(artists) { artist ->
                                ArtistListItem(artist = artist, onClick = { selectedArtistForView = artist })
                            }
                        }
                    } else {
                        val artistSongs = songs.filter { it.artist.equals(selectedArtistForView?.name, ignoreCase = true) }
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { selectedArtistForView = null }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PulseTextPrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedArtistForView!!.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = PulseTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${artistSongs.size} tracks",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PulseTextSecondary
                                    )
                                }
                                Button(
                                    onClick = { onSongClick(artistSongs, 0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Play All", color = Color.White)
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 140.dp)
                            ) {
                                items(artistSongs) { song ->
                                    SongItem(
                                        song = song,
                                        isPlaying = isPlaying && currentSong?.id == song.id,
                                        isCurrent = currentSong?.id == song.id,
                                        onClick = { onSongClick(artistSongs, artistSongs.indexOf(song)) },
                                        onFavoriteToggle = { onFavoriteToggle(song) },
                                        onMoreClick = { selectedSongForDetails = song }
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Folders List or Folder Detail
                    if (selectedFolderForView == null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 140.dp)
                        ) {
                            items(folders) { folder ->
                                FolderListItem(folder = folder, onClick = { selectedFolderForView = folder })
                            }
                        }
                    } else {
                        val folderSongs = songs.filter { (it.relativePath ?: "") == selectedFolderForView?.path }
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { selectedFolderForView = null }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PulseTextPrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedFolderForView!!.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = PulseTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${folderSongs.size} tracks",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PulseTextSecondary
                                    )
                                }
                                Button(
                                    onClick = { onSongClick(folderSongs, 0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Play All", color = Color.White)
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 140.dp)
                            ) {
                                items(folderSongs) { song ->
                                    SongItem(
                                        song = song,
                                        isPlaying = isPlaying && currentSong?.id == song.id,
                                        isCurrent = currentSong?.id == song.id,
                                        onClick = { onSongClick(folderSongs, folderSongs.indexOf(song)) },
                                        onFavoriteToggle = { onFavoriteToggle(song) },
                                        onMoreClick = { selectedSongForDetails = song }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Song Details & Actions Dialog
    if (selectedSongForDetails != null) {
        val s = selectedSongForDetails!!
        AlertDialog(
            onDismissRequest = { selectedSongForDetails = null },
            containerColor = PulseCardDark,
            title = {
                Text(text = s.title, color = PulseTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Artist: ${s.artist}", color = PulseTextSecondary)
                    Text("Album: ${s.album}", color = PulseTextSecondary)
                    Text("Duration: ${formatDuration(s.duration)}", color = PulseTextSecondary)
                    Text("Path: ${s.relativePath ?: "Unknown"}", color = PulseTextSecondary)
                    Text("Size: ${s.size / (1024 * 1024)} MB", color = PulseTextSecondary)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddToPlaylistClick(s)
                        selectedSongForDetails = null
                    }
                ) {
                    Text("Add to Playlist", color = PulseRedAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSongForDetails = null }) {
                    Text("Close", color = PulseTextMuted)
                }
            }
        )
    }
}

@Composable
fun AlbumGridItem(album: Album, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = PulseCardDark
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(PulseSecondaryDark),
                contentAlignment = Alignment.Center
            ) {
                if (!album.albumArtUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = album.albumArtUri,
                        contentDescription = album.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = PulseTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.artist} • ${album.songCount} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ArtistListItem(artist: Artist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PulseRedAccent,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PulseTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${artist.trackCount} tracks • ${artist.albumCount} albums",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = PulseTextMuted
        )
    }
}

@Composable
fun FolderListItem(folder: MusicFolder, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = PulseTextPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PulseTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${folder.songCount} tracks • ${folder.path}",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = PulseTextMuted
        )
    }
}
