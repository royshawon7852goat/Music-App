package com.pulse.music.ui.home

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pulse.music.data.models.Song
import com.pulse.music.ui.components.PulseHeader
import com.pulse.music.ui.components.SongItem
import com.pulse.music.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    allSongs: List<Song>,
    recentlyPlayed: List<Song>,
    recentlyAdded: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    isScanning: Boolean,
    onSongClick: (List<Song>, Int) -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onQuickAccessClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRescanClick: () -> Unit
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            PulseHeader(
                title = "PULSE",
                subtitle = "Your Music. Your Rhythm.",
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick
            )
        }

        // Greeting Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = PulseTextPrimary
                )
                Text(
                    text = "Ready to listen?",
                    style = MaterialTheme.typography.titleMedium,
                    color = PulseTextSecondary
                )
            }
        }

        // Quick Access Cards Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessCard(
                        title = "All Songs",
                        subtitle = "${allSongs.size} tracks",
                        icon = Icons.Default.MusicNote,
                        modifier = Modifier.weight(1f),
                        onClick = { onQuickAccessClick("all_songs") }
                    )
                    QuickAccessCard(
                        title = "Favorites",
                        subtitle = "Loved tracks",
                        icon = Icons.Default.Favorite,
                        iconTint = PulseRedAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onQuickAccessClick("favorites") }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessCard(
                        title = "Albums",
                        subtitle = "Browse covers",
                        icon = Icons.Default.Album,
                        modifier = Modifier.weight(1f),
                        onClick = { onQuickAccessClick("albums") }
                    )
                    QuickAccessCard(
                        title = "Playlists",
                        subtitle = "Curated",
                        icon = Icons.Default.QueueMusic,
                        modifier = Modifier.weight(1f),
                        onClick = { onQuickAccessClick("playlists") }
                    )
                }
            }
        }

        // Recently Played Section
        if (recentlyPlayed.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Played",
                        style = MaterialTheme.typography.titleLarge,
                        color = PulseTextPrimary
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentlyPlayed.take(10)) { song ->
                        RecentlyPlayedCard(
                            song = song,
                            isCurrent = currentSong?.id == song.id,
                            isPlaying = isPlaying && currentSong?.id == song.id,
                            onClick = { onSongClick(recentlyPlayed, recentlyPlayed.indexOf(song)) }
                        )
                    }
                }
            }
        }

        // Recently Added Section
        if (recentlyAdded.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Added",
                        style = MaterialTheme.typography.titleLarge,
                        color = PulseTextPrimary
                    )
                }
            }

            items(recentlyAdded.take(5)) { song ->
                SongItem(
                    song = song,
                    isPlaying = isPlaying && currentSong?.id == song.id,
                    isCurrent = currentSong?.id == song.id,
                    onClick = { onSongClick(recentlyAdded, recentlyAdded.indexOf(song)) },
                    onFavoriteToggle = { onFavoriteToggle(song) }
                )
            }
        }

        // Empty state when no songs found
        if (allSongs.isEmpty() && !isScanning) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicOff,
                            contentDescription = null,
                            tint = PulseTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No Music Found",
                            style = MaterialTheme.typography.titleLarge,
                            color = PulseTextPrimary
                        )
                        Text(
                            text = "Pulse plays offline audio files stored on your device. Scan your storage to discover tracks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onRescanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Scan Storage", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = PulseTextPrimary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = PulseCardDark
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PulseSecondaryDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RecentlyPlayedCard(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            if (!song.albumArtUri.isNullOrEmpty()) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PulseTextMuted,
                    modifier = Modifier.size(48.dp)
                )
            }

            if (isCurrent && isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Playing",
                        tint = PulseRedAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isCurrent) PulseRedAccent else PulseTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodyMedium,
            color = PulseTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
