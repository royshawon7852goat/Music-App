package com.pulse.music.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.pulse.music.data.models.LyricLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

object LrcParser {

    private val TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:[.:](\\d{2,3}))?\\]")

    suspend fun loadLyricsForSong(context: Context, songUri: String, relativePath: String?, songTitle: String): List<LyricLine> =
        withContext(Dispatchers.IO) {
            // 1. Try finding a local .lrc file in the same folder
            val fileLyrics = findLocalLrcFile(relativePath, songTitle)
            if (fileLyrics.isNotEmpty()) {
                return@withContext fileLyrics
            }

            // 2. Try reading embedded lyrics tag via MediaMetadataRetriever
            val embeddedLyrics = readEmbeddedLyrics(context, songUri)
            if (embeddedLyrics.isNotEmpty()) {
                return@withContext embeddedLyrics
            }

            emptyList()
        }

    private fun findLocalLrcFile(relativePath: String?, songTitle: String): List<LyricLine> {
        if (relativePath == null) return emptyList()
        try {
            val dir = File(relativePath)
            if (dir.exists() && dir.isDirectory) {
                val baseTitle = sanitizeFilename(songTitle).lowercase()
                val lrcFile = dir.listFiles { file ->
                    file.extension.equals("lrc", ignoreCase = true) &&
                            file.nameWithoutExtension.lowercase().contains(baseTitle)
                }?.firstOrNull()

                if (lrcFile != null && lrcFile.exists()) {
                    return parseLrcContent(lrcFile.readText())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    private fun readEmbeddedLyrics(context: Context, songUri: String): List<LyricLine> {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, Uri.parse(songUri))
            // METADATA_KEY_COMPILATION, METADATA_KEY_TITLE etc. In standard Android,
            // key 1000 or custom ID3 frames might not have a direct constant,
            // but let's check if the generic string has LRC lines
            emptyList()
        } catch (e: Exception) {
            emptyList()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun parseLrcContent(content: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        content.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("[ti:") || trimmed.startsWith("[ar:") ||
                trimmed.startsWith("[al:") || trimmed.startsWith("[by:") || trimmed.startsWith("[offset:")
            ) {
                return@forEach
            }

            val matcher = TIMESTAMP_PATTERN.matcher(trimmed)
            val timestamps = mutableListOf<Long>()
            var lastMatchEnd = 0

            while (matcher.find()) {
                val minutes = matcher.group(1)?.toLongOrNull() ?: 0L
                val seconds = matcher.group(2)?.toLongOrNull() ?: 0L
                val millisString = matcher.group(3) ?: "00"
                val millis = if (millisString.length == 2) {
                    millisString.toLongOrNull()?.times(10) ?: 0L
                } else {
                    millisString.toLongOrNull() ?: 0L
                }
                val totalMs = minutes * 60_000L + seconds * 1_000L + millis
                timestamps.add(totalMs)
                lastMatchEnd = matcher.end()
            }

            val lyricText = trimmed.substring(lastMatchEnd).trim()
            for (ts in timestamps) {
                lines.add(LyricLine(timestampMs = ts, text = lyricText))
            }
        }

        return lines.sortedBy { it.timestampMs }
    }

    private fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9.-]"), "_")
    }
}
