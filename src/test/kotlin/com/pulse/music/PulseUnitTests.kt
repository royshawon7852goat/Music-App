package com.pulse.music

import com.pulse.music.data.models.*
import com.pulse.music.ui.components.formatDuration
import com.pulse.music.utils.AudioScanner
import com.pulse.music.utils.LrcParser
import org.junit.Assert.*
import org.junit.Test

class PulseUnitTests {

    @Test
    fun testFormatDuration() {
        assertEquals("0:00", formatDuration(0L))
        assertEquals("0:45", formatDuration(45_000L))
        assertEquals("3:24", formatDuration(204_000L))
        assertEquals("10:05", formatDuration(605_000L))
    }

    @Test
    fun testLrcParser() {
        val sampleLrc = """
            [ti:Test Song]
            [ar:Test Artist]
            [00:12.50]Line 1 of lyrics
            [00:25.00]Line 2 of lyrics
            [01:05.75]Chorus line
        """.trimIndent()

        val parsed = LrcParser.parseLrcContent(sampleLrc)
        assertEquals(3, parsed.size)
        assertEquals(12_500L, parsed[0].timestampMs)
        assertEquals("Line 1 of lyrics", parsed[0].text)
        assertEquals(25_000L, parsed[1].timestampMs)
        assertEquals("Line 2 of lyrics", parsed[1].text)
        assertEquals(65_750L, parsed[2].timestampMs)
        assertEquals("Chorus line", parsed[2].text)
    }

    @Test
    fun testAudioScannerGrouping() {
        val songs = listOf(
            Song(1, "Track 1", "Artist A", "Album X", 180000, "content://1", relativePath = "/Music/Rock"),
            Song(2, "Track 2", "Artist A", "Album X", 200000, "content://2", relativePath = "/Music/Rock"),
            Song(3, "Track 3", "Artist B", "Album Y", 210000, "content://3", relativePath = "/Music/Pop")
        )

        val albums = AudioScanner.groupIntoAlbums(songs)
        assertEquals(2, albums.size)
        assertEquals("Album X", albums[0].title)
        assertEquals(2, albums[0].songCount)
        assertEquals("Album Y", albums[1].title)
        assertEquals(1, albums[1].songCount)

        val artists = AudioScanner.groupIntoArtists(songs)
        assertEquals(2, artists.size)
        assertEquals("Artist A", artists[0].name)
        assertEquals(2, artists[0].trackCount)
        assertEquals("Artist B", artists[1].name)
        assertEquals(1, artists[1].trackCount)

        val folders = AudioScanner.groupIntoFolders(songs)
        assertEquals(2, folders.size)
    }

    @Test
    fun testRepeatModeTransitions() {
        var mode = RepeatMode.OFF
        mode = when (mode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        assertEquals(RepeatMode.ALL, mode)

        mode = when (mode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        assertEquals(RepeatMode.ONE, mode)

        mode = when (mode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        assertEquals(RepeatMode.OFF, mode)
    }
}
