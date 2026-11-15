package com.pulse.music.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.pulse.music.data.database.SongEntity
import com.pulse.music.data.models.Album
import com.pulse.music.data.models.Artist
import com.pulse.music.data.models.MusicFolder
import com.pulse.music.data.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AudioScanner {

    private val SUPPORTED_MIME_TYPES = setOf(
        "audio/mpeg",
        "audio/mp3",
        "audio/flac",
        "audio/x-flac",
        "audio/wav",
        "audio/x-wav",
        "audio/aac",
        "audio/mp4",
        "audio/m4a",
        "audio/ogg",
        "audio/x-ogg",
        "audio/opus",
        "application/ogg"
    )

    private val SUPPORTED_EXTENSIONS = setOf(
        "mp3", "flac", "wav", "aac", "m4a", "ogg", "opus"
    )

    suspend fun scanDeviceAudio(context: Context): List<Song> = withContext(Dispatchers.IO) {
        val songList = mutableListOf<Song>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.RELATIVE_PATH
            } else {
                MediaStore.Audio.Media.DATA
            }
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 10000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn).let {
                        if (it == null || it == "<unknown>") "Unknown Artist" else it
                    }
                    val album = cursor.getString(albumColumn).let {
                        if (it == null || it == "<unknown>") "Unknown Album" else it
                    }
                    val albumId = cursor.getLong(albumIdColumn)
                    val duration = cursor.getLong(durationColumn)
                    val dataPath = cursor.getString(dataColumn) ?: ""
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    // Verify file extension if path is available
                    val ext = dataPath.substringAfterLast('.', "").lowercase()
                    if (ext.isNotEmpty() && ext !in SUPPORTED_EXTENSIONS) {
                        continue
                    }

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val albumArtUri = "content://media/external/audio/albumart/$albumId"

                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val relCol = cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
                        if (relCol != -1) cursor.getString(relCol) else File(dataPath).parent
                    } else {
                        File(dataPath).parent
                    }

                    songList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            contentUri = contentUri,
                            albumArtUri = albumArtUri,
                            relativePath = relativePath,
                            size = size,
                            dateAdded = dateAdded
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songList
    }

    fun groupIntoAlbums(songs: List<Song>): List<Album> {
        return songs.groupBy { it.album }
            .map { (albumTitle, songList) ->
                val firstSong = songList.first()
                Album(
                    id = albumTitle.hashCode().toLong(),
                    title = albumTitle,
                    artist = firstSong.artist,
                    songCount = songList.size,
                    albumArtUri = firstSong.albumArtUri
                )
            }.sortedBy { it.title.lowercase() }
    }

    fun groupIntoArtists(songs: List<Song>): List<Artist> {
        return songs.groupBy { it.artist }
            .map { (artistName, songList) ->
                val uniqueAlbums = songList.map { it.album }.distinct().size
                Artist(
                    id = artistName.hashCode().toLong(),
                    name = artistName,
                    trackCount = songList.size,
                    albumCount = uniqueAlbums
                )
            }.sortedBy { it.name.lowercase() }
    }

    fun groupIntoFolders(songs: List<Song>): List<MusicFolder> {
        return songs.groupBy { it.relativePath ?: "Internal Storage" }
            .map { (path, songList) ->
                val folderName = path.trimEnd('/').substringAfterLast('/', path)
                MusicFolder(
                    name = if (folderName.isEmpty()) "Root" else folderName,
                    path = path,
                    songCount = songList.size
                )
            }.sortedBy { it.name.lowercase() }
    }

    fun Song.toEntity(isFavorite: Boolean = false, playCount: Int = 0, lastPlayed: Long = 0L): SongEntity {
        return SongEntity(
            songId = id,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            contentUri = contentUri,
            albumArtUri = albumArtUri,
            relativePath = relativePath,
            size = size,
            dateAdded = dateAdded,
            isFavorite = isFavorite,
            playCount = playCount,
            lastPlayedTimestamp = lastPlayed
        )
    }

    fun SongEntity.toModel(): Song {
        return Song(
            id = songId,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            contentUri = contentUri,
            albumArtUri = albumArtUri,
            relativePath = relativePath,
            size = size,
            dateAdded = dateAdded,
            isFavorite = isFavorite,
            playCount = playCount,
            lastPlayedTimestamp = lastPlayedTimestamp
        )
    }
}
