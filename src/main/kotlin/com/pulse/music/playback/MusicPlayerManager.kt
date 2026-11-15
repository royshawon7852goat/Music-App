package com.pulse.music.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.pulse.music.data.models.RepeatMode
import com.pulse.music.data.models.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min

@OptIn(UnstableApi::class)
class MusicPlayerManager private constructor(private val context: Context) : Player.Listener {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            androidx.media3.common.AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true // handle audio focus automatically
        )
        .setHandleAudioBecomingNoisy(true)
        .build()

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // State flows
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    // Sleep timer
    private val _sleepTimerRemainingMs = MutableStateFlow<Long?>(null)
    val sleepTimerRemainingMs: StateFlow<Long?> = _sleepTimerRemainingMs.asStateFlow()
    private var sleepTimerJob: Job? = null

    // Equalizer
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private val _isEqualizerEnabled = MutableStateFlow(false)
    val isEqualizerEnabled: StateFlow<Boolean> = _isEqualizerEnabled.asStateFlow()

    private val _bassBoostStrength = MutableStateFlow(0)
    val bassBoostStrength: StateFlow<Int> = _bassBoostStrength.asStateFlow()

    private val _equalizerPresets = MutableStateFlow<List<String>>(emptyList())
    val equalizerPresets: StateFlow<List<String>> = _equalizerPresets.asStateFlow()

    private val _currentPreset = MutableStateFlow<Int>(-1)
    val currentPreset: StateFlow<Int> = _currentPreset.asStateFlow()

    private val _bandLevels = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val bandLevels: StateFlow<Map<Int, Int>> = _bandLevels.asStateFlow()

    private val _bandFrequencies = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val bandFrequencies: StateFlow<Map<Int, Int>> = _bandFrequencies.asStateFlow()

    private var originalQueue = listOf<Song>()
    private var positionUpdateJob: Job? = null

    init {
        exoPlayer.addListener(this)
        startPositionUpdates()
        setupAudioEffects()
    }

    private fun setupAudioEffects() {
        try {
            val audioSessionId = exoPlayer.audioSessionId
            if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                equalizer = Equalizer(0, audioSessionId).apply {
                    enabled = false
                }
                bassBoost = BassBoost(0, audioSessionId).apply {
                    enabled = false
                }

                equalizer?.let { eq ->
                    val presets = mutableListOf<String>()
                    for (i in 0 until eq.numberOfPresets) {
                        presets.add(eq.getPresetName(i.toShort()))
                    }
                    _equalizerPresets.value = presets

                    val freqs = mutableMapOf<Int, Int>()
                    val levels = mutableMapOf<Int, Int>()
                    for (band in 0 until eq.numberOfBands) {
                        freqs[band] = eq.getCenterFreq(band.toShort())
                        levels[band] = eq.getBandLevel(band.toShort()).toInt()
                    }
                    _bandFrequencies.value = freqs
                    _bandLevels.value = levels
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
            _isEqualizerEnabled.value = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(strength: Int) { // 0 - 1000
        try {
            val clamped = max(0, min(1000, strength))
            bassBoost?.setStrength(clamped.toShort())
            _bassBoostStrength.value = clamped
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setPreset(presetIndex: Int) {
        try {
            equalizer?.usePreset(presetIndex.toShort())
            _currentPreset.value = presetIndex
            equalizer?.let { eq ->
                val levels = mutableMapOf<Int, Int>()
                for (band in 0 until eq.numberOfBands) {
                    levels[band] = eq.getBandLevel(band.toShort()).toInt()
                }
                _bandLevels.value = levels
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(band: Int, level: Int) {
        try {
            equalizer?.setBandLevel(band.toShort(), level.toShort())
            val updated = _bandLevels.value.toMutableMap()
            updated[band] = level
            _bandLevels.value = updated
            _currentPreset.value = -1 // custom
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetEqualizer() {
        try {
            equalizer?.let { eq ->
                for (band in 0 until eq.numberOfBands) {
                    eq.setBandLevel(band.toShort(), 0)
                }
                val levels = mutableMapOf<Int, Int>()
                for (band in 0 until eq.numberOfBands) {
                    levels[band] = 0
                }
                _bandLevels.value = levels
            }
            bassBoost?.setStrength(0)
            _bassBoostStrength.value = 0
            _currentPreset.value = -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSong(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty() || startIndex !in songs.indices) return

        originalQueue = songs
        _queue.value = songs
        _currentIndex.value = startIndex

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setArtworkUri(song.albumArtUri?.let { Uri.parse(it) })
                        .build()
                )
                .build()
        }

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()
        exoPlayer.play()

        _currentSong.value = songs[startIndex]
        _duration.value = songs[startIndex].duration
        _isPlaying.value = true
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE && _currentSong.value != null) {
                exoPlayer.prepare()
            }
            exoPlayer.play()
        }
    }

    fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        } else if (_repeatMode.value == RepeatMode.ALL && _queue.value.isNotEmpty()) {
            exoPlayer.seekTo(0, 0L)
        }
    }

    fun previous() {
        if (exoPlayer.currentPosition > 3000L) {
            exoPlayer.seekTo(0L)
        } else if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            exoPlayer.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun toggleShuffle() {
        val newShuffle = !_isShuffleEnabled.value
        _isShuffleEnabled.value = newShuffle
        exoPlayer.shuffleModeEnabled = newShuffle
    }

    fun toggleRepeat() {
        val nextMode = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _repeatMode.value = nextMode
        exoPlayer.repeatMode = when (nextMode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
    }

    fun startSleepTimer(minutes: Int) {
        cancelSleepTimer()
        val totalMs = minutes * 60_000L
        _sleepTimerRemainingMs.value = totalMs

        sleepTimerJob = scope.launch {
            var remaining = totalMs
            val fadeThresholdMs = 30_000L // last 30 seconds fade out

            while (remaining > 0) {
                delay(1000L)
                remaining -= 1000L
                _sleepTimerRemainingMs.value = remaining

                if (remaining in 1..fadeThresholdMs) {
                    val volumeFactor = remaining.toFloat() / fadeThresholdMs.toFloat()
                    exoPlayer.volume = volumeFactor
                }
            }

            // Time's up
            exoPlayer.pause()
            exoPlayer.volume = 1.0f
            _sleepTimerRemainingMs.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerRemainingMs.value = null
        exoPlayer.volume = 1.0f
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _currentPosition.value = exoPlayer.currentPosition
                    _duration.value = if (exoPlayer.duration > 0) exoPlayer.duration else _currentSong.value?.duration ?: 0L
                }
                delay(250L)
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _duration.value = exoPlayer.duration
                _isPlaying.value = exoPlayer.isPlaying
            }
            Player.STATE_ENDED -> {
                _isPlaying.value = false
            }
            Player.STATE_BUFFERING -> {}
            Player.STATE_IDLE -> {
                _isPlaying.value = false
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        val index = exoPlayer.currentMediaItemIndex
        _currentIndex.value = index
        if (index in _queue.value.indices) {
            _currentSong.value = _queue.value[index]
            _duration.value = _queue.value[index].duration
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        error.printStackTrace()
        // Automatically skip to next track on corrupted or missing audio files
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    fun release() {
        positionUpdateJob?.cancel()
        sleepTimerJob?.cancel()
        equalizer?.release()
        bassBoost?.release()
        exoPlayer.removeListener(this)
        exoPlayer.release()
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerManager? = null

        fun getInstance(context: Context): MusicPlayerManager {
            return INSTANCE ?: synchronized(this) {
                val instance = MusicPlayerManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
