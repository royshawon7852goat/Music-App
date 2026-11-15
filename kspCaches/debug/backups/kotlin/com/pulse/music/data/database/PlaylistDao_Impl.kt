package com.pulse.music.`data`.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PlaylistDao_Impl(
  __db: RoomDatabase,
) : PlaylistDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPlaylistEntity: EntityInsertAdapter<PlaylistEntity>

  private val __insertAdapterOfPlaylistSongCrossRef: EntityInsertAdapter<PlaylistSongCrossRef>

  private val __deleteAdapterOfPlaylistEntity: EntityDeleteOrUpdateAdapter<PlaylistEntity>

  private val __updateAdapterOfPlaylistEntity: EntityDeleteOrUpdateAdapter<PlaylistEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPlaylistEntity = object : EntityInsertAdapter<PlaylistEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `playlists` (`playlistId`,`name`,`createdAt`,`isDefault`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistEntity) {
        statement.bindLong(1, entity.playlistId)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.createdAt)
        val _tmp: Int = if (entity.isDefault) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }
    this.__insertAdapterOfPlaylistSongCrossRef = object : EntityInsertAdapter<PlaylistSongCrossRef>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `playlist_songs` (`playlistId`,`songId`,`sortOrder`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistSongCrossRef) {
        statement.bindLong(1, entity.playlistId)
        statement.bindLong(2, entity.songId)
        statement.bindLong(3, entity.sortOrder.toLong())
      }
    }
    this.__deleteAdapterOfPlaylistEntity = object : EntityDeleteOrUpdateAdapter<PlaylistEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `playlists` WHERE `playlistId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistEntity) {
        statement.bindLong(1, entity.playlistId)
      }
    }
    this.__updateAdapterOfPlaylistEntity = object : EntityDeleteOrUpdateAdapter<PlaylistEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `playlists` SET `playlistId` = ?,`name` = ?,`createdAt` = ?,`isDefault` = ? WHERE `playlistId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistEntity) {
        statement.bindLong(1, entity.playlistId)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.createdAt)
        val _tmp: Int = if (entity.isDefault) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.playlistId)
      }
    }
  }

  public override suspend fun insertPlaylist(playlist: PlaylistEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPlaylistEntity.insertAndReturnId(_connection, playlist)
    _result
  }

  public override suspend fun insertPlaylistSong(crossRef: PlaylistSongCrossRef): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPlaylistSongCrossRef.insert(_connection, crossRef)
  }

  public override suspend fun deletePlaylist(playlist: PlaylistEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfPlaylistEntity.handle(_connection, playlist)
  }

  public override suspend fun updatePlaylist(playlist: PlaylistEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfPlaylistEntity.handle(_connection, playlist)
  }

  public override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
    val _sql: String = "SELECT * FROM playlists ORDER BY isDefault DESC, name ASC"
    return createFlow(__db, false, arrayOf("playlists")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPlaylistId: Int = getColumnIndexOrThrow(_stmt, "playlistId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsDefault: Int = getColumnIndexOrThrow(_stmt, "isDefault")
        val _result: MutableList<PlaylistEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlaylistEntity
          val _tmpPlaylistId: Long
          _tmpPlaylistId = _stmt.getLong(_columnIndexOfPlaylistId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsDefault: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDefault).toInt()
          _tmpIsDefault = _tmp != 0
          _item = PlaylistEntity(_tmpPlaylistId,_tmpName,_tmpCreatedAt,_tmpIsDefault)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPlaylistById(playlistId: Long): PlaylistEntity? {
    val _sql: String = "SELECT * FROM playlists WHERE playlistId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        val _columnIndexOfPlaylistId: Int = getColumnIndexOrThrow(_stmt, "playlistId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsDefault: Int = getColumnIndexOrThrow(_stmt, "isDefault")
        val _result: PlaylistEntity?
        if (_stmt.step()) {
          val _tmpPlaylistId: Long
          _tmpPlaylistId = _stmt.getLong(_columnIndexOfPlaylistId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsDefault: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDefault).toInt()
          _tmpIsDefault = _tmp != 0
          _result = PlaylistEntity(_tmpPlaylistId,_tmpName,_tmpCreatedAt,_tmpIsDefault)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>> {
    val _sql: String = """
        |
        |        SELECT songs.* FROM songs
        |        INNER JOIN playlist_songs ON songs.songId = playlist_songs.songId
        |        WHERE playlist_songs.playlistId = ?
        |        ORDER BY playlist_songs.sortOrder ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("songs", "playlist_songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        val _columnIndexOfSongId: Int = getColumnIndexOrThrow(_stmt, "songId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfArtist: Int = getColumnIndexOrThrow(_stmt, "artist")
        val _columnIndexOfAlbum: Int = getColumnIndexOrThrow(_stmt, "album")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfContentUri: Int = getColumnIndexOrThrow(_stmt, "contentUri")
        val _columnIndexOfAlbumArtUri: Int = getColumnIndexOrThrow(_stmt, "albumArtUri")
        val _columnIndexOfRelativePath: Int = getColumnIndexOrThrow(_stmt, "relativePath")
        val _columnIndexOfSize: Int = getColumnIndexOrThrow(_stmt, "size")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfIsFavorite: Int = getColumnIndexOrThrow(_stmt, "isFavorite")
        val _columnIndexOfPlayCount: Int = getColumnIndexOrThrow(_stmt, "playCount")
        val _columnIndexOfLastPlayedTimestamp: Int = getColumnIndexOrThrow(_stmt, "lastPlayedTimestamp")
        val _result: MutableList<SongEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SongEntity
          val _tmpSongId: Long
          _tmpSongId = _stmt.getLong(_columnIndexOfSongId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpArtist: String
          _tmpArtist = _stmt.getText(_columnIndexOfArtist)
          val _tmpAlbum: String
          _tmpAlbum = _stmt.getText(_columnIndexOfAlbum)
          val _tmpDuration: Long
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration)
          val _tmpContentUri: String
          _tmpContentUri = _stmt.getText(_columnIndexOfContentUri)
          val _tmpAlbumArtUri: String?
          if (_stmt.isNull(_columnIndexOfAlbumArtUri)) {
            _tmpAlbumArtUri = null
          } else {
            _tmpAlbumArtUri = _stmt.getText(_columnIndexOfAlbumArtUri)
          }
          val _tmpRelativePath: String?
          if (_stmt.isNull(_columnIndexOfRelativePath)) {
            _tmpRelativePath = null
          } else {
            _tmpRelativePath = _stmt.getText(_columnIndexOfRelativePath)
          }
          val _tmpSize: Long
          _tmpSize = _stmt.getLong(_columnIndexOfSize)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpIsFavorite: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsFavorite).toInt()
          _tmpIsFavorite = _tmp != 0
          val _tmpPlayCount: Int
          _tmpPlayCount = _stmt.getLong(_columnIndexOfPlayCount).toInt()
          val _tmpLastPlayedTimestamp: Long
          _tmpLastPlayedTimestamp = _stmt.getLong(_columnIndexOfLastPlayedTimestamp)
          _item = SongEntity(_tmpSongId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDuration,_tmpContentUri,_tmpAlbumArtUri,_tmpRelativePath,_tmpSize,_tmpDateAdded,_tmpIsFavorite,_tmpPlayCount,_tmpLastPlayedTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSongCountForPlaylist(playlistId: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM playlist_songs WHERE playlistId = ?"
    return createFlow(__db, false, arrayOf("playlist_songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deletePlaylistById(playlistId: Long) {
    val _sql: String = "DELETE FROM playlists WHERE playlistId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
    val _sql: String = "DELETE FROM playlist_songs WHERE playlistId = ? AND songId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, songId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearPlaylistSongs(playlistId: Long) {
    val _sql: String = "DELETE FROM playlist_songs WHERE playlistId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, playlistId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
