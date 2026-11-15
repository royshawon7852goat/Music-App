package com.pulse.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pulse.music.data.models.LyricLine
import com.pulse.music.data.models.RepeatMode
import com.pulse.music.data.models.Song
import com.pulse.music.ui.components.formatDuration
import com.pulse.music.ui.theme.*
import com.pulse.music.utils.LrcParser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    song: Song?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    queue: List<Song>,
    currentIndex: Int,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onQueueItemClick: (Int) -> Unit,
    onEqualizerClick: () -> Unit,
    onSleepTimerClick: () -> Unit
) {
    if (song == null) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showLyrics by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }
    var lyrics by remember { mutableStateOf<List<LyricLine>>(emptyList()) }
    var isLoadingLyrics by remember { mutableStateOf(false) }

    // Load lyrics whenever song changes
    LaunchedEffect(song.id) {
        isLoadingLyrics = true
        lyrics = LrcParser.loadLyricsForSong(
            context = context,
            songUri = song.contentUri,
            relativePath = song.relativePath,
            songTitle = song.title
        )
        isLoadingLyrics = false
    }

    // Artwork breathing animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "pulseArtwork")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.03f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "scale"
    )

    // User seeking state
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekPositionSlider by remember { mutableFloatStateOf(0f) }

    val currentSliderPosition = if (isUserSeeking) {
        seekPositionSlider
    } else {
        if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    }

    Scaffold(
        containerColor = PulseBlack,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PulseCardDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = PulseTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NOW PLAYING",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                            color = PulseTextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PulseRedAccent)
                        )
                    }
                    Text(
                        text = song.album,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onEqualizerClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PulseCardDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer",
                            tint = PulseTextPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Main Visual: Artwork OR Lyrics
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (showLyrics) {
                    // Synchronized Lyrics View
                    LyricsContainer(
                        lyrics = lyrics,
                        currentPosition = currentPosition,
                        isLoading = isLoadingLyrics,
                        onSeekTo = onSeekTo
                    )
                } else if (showQueue) {
                    // Queue View
                    QueueListContainer(
                        queue = queue,
                        currentIndex = currentIndex,
                        isPlaying = isPlaying,
                        onSongClick = onQueueItemClick
                    )
                } else {
                    // Large Square Album Artwork with subtle glow & breathing animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .aspectRatio(1f)
                            .scale(pulseScale)
                            .clip(RoundedCornerShape(28.dp))
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
                                modifier = Modifier.size(96.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Song Info & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = PulseTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { onFavoriteToggle(song) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PulseCardDark)
                ) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) PulseRedAccent else PulseTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline Seek Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentSliderPosition,
                    onValueChange = {
                        isUserSeeking = true
                        seekPositionSlider = it
                    },
                    onValueChangeFinished = {
                        isUserSeeking = false
                        onSeekTo((seekPositionSlider * duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = PulseRedAccent,
                        activeTrackColor = PulseProgressBar,
                        inactiveTrackColor = PulseBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayedPosition = if (isUserSeeking) {
                        (seekPositionSlider * duration).toLong()
                    } else {
                        currentPosition
                    }
                    Text(
                        text = formatDuration(displayedPosition),
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle button
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) PulseRedAccent else PulseTextMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PulseCardDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = PulseTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play / Pause (Signature large circular button with Pulse Red)
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(PulseRedAccent)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PulseCardDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = PulseTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Repeat button
                IconButton(onClick = onToggleRepeat) {
                    val (icon, tint) = when (repeatMode) {
                        RepeatMode.OFF -> Pair(Icons.Default.Repeat, PulseTextMuted)
                        RepeatMode.ALL -> Pair(Icons.Default.Repeat, PulseRedAccent)
                        RepeatMode.ONE -> Pair(Icons.Default.RepeatOne, PulseRedAccent)
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat",
                        tint = tint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Auxiliary Actions: Lyrics, Queue, Sleep Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lyrics toggle
                TextButton(
                    onClick = {
                        showLyrics = !showLyrics
                        if (showLyrics) showQueue = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (showLyrics) PulseRedAccent else PulseTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lyrics")
                }

                // Queue toggle
                TextButton(
                    onClick = {
                        showQueue = !showQueue
                        if (showQueue) showLyrics = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (showQueue) PulseRedAccent else PulseTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Queue (${queue.size})")
                }

                // Sleep Timer
                TextButton(
                    onClick = onSleepTimerClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = PulseTextMuted)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Timer")
                }
            }
        }
    }
}

@Composable
fun LyricsContainer(
    lyrics: List<LyricLine>,
    currentPosition: Long,
    isLoading: Boolean,
    onSeekTo: (Long) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp)),
        color = PulseCardDark
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PulseRedAccent)
            }
        } else if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SubtitlesOff,
                        contentDescription = null,
                        tint = PulseTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No lyrics available.",
                        style = MaterialTheme.typography.titleMedium,
                        color = PulseTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add a .lrc file in the song folder to display synchronized lyrics.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()
            val activeIndex = remember(lyrics, currentPosition) {
                val idx = lyrics.indexOfLast { it.timestampMs <= currentPosition }
                if (idx >= 0) idx else 0
            }

            LaunchedEffect(activeIndex) {
                if (lyrics.isNotEmpty() && activeIndex in lyrics.indices) {
                    listState.animateScrollToItem(maxOf(0, activeIndex - 2))
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeIndex
                    Text(
                        text = line.text,
                        style = if (isActive) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        else MaterialTheme.typography.bodyLarge,
                        color = if (isActive) PulseRedAccent else PulseTextMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeekTo(line.timestampMs) }
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun QueueListContainer(
    queue: List<Song>,
    currentIndex: Int,
    isPlaying: Boolean,
    onSongClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp)),
        color = PulseCardDark
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Playing Queue",
                style = MaterialTheme.typography.titleLarge,
                color = PulseTextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(queue) { index, song ->
                    val isCurrent = index == currentIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) PulseSecondaryDark else Color.Transparent)
                            .clickable { onSongClick(index) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrent) PulseRedAccent else PulseTextMuted,
                            modifier = Modifier.width(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.titleMedium,
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
                        if (isCurrent && isPlaying) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Playing",
                                tint = PulseRedAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
