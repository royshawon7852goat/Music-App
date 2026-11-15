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
public class SongDao_Impl(
  __db: RoomDatabase,
) : SongDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSongEntity: EntityInsertAdapter<SongEntity>

  private val __updateAdapterOfSongEntity: EntityDeleteOrUpdateAdapter<SongEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSongEntity = object : EntityInsertAdapter<SongEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `songs` (`songId`,`title`,`artist`,`album`,`duration`,`contentUri`,`albumArtUri`,`relativePath`,`size`,`dateAdded`,`isFavorite`,`playCount`,`lastPlayedTimestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SongEntity) {
        statement.bindLong(1, entity.songId)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.artist)
        statement.bindText(4, entity.album)
        statement.bindLong(5, entity.duration)
        statement.bindText(6, entity.contentUri)
        val _tmpAlbumArtUri: String? = entity.albumArtUri
        if (_tmpAlbumArtUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpAlbumArtUri)
        }
        val _tmpRelativePath: String? = entity.relativePath
        if (_tmpRelativePath == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRelativePath)
        }
        statement.bindLong(9, entity.size)
        statement.bindLong(10, entity.dateAdded)
        val _tmp: Int = if (entity.isFavorite) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.playCount.toLong())
        statement.bindLong(13, entity.lastPlayedTimestamp)
      }
    }
    this.__updateAdapterOfSongEntity = object : EntityDeleteOrUpdateAdapter<SongEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `songs` SET `songId` = ?,`title` = ?,`artist` = ?,`album` = ?,`duration` = ?,`contentUri` = ?,`albumArtUri` = ?,`relativePath` = ?,`size` = ?,`dateAdded` = ?,`isFavorite` = ?,`playCount` = ?,`lastPlayedTimestamp` = ? WHERE `songId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SongEntity) {
        statement.bindLong(1, entity.songId)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.artist)
        statement.bindText(4, entity.album)
        statement.bindLong(5, entity.duration)
        statement.bindText(6, entity.contentUri)
        val _tmpAlbumArtUri: String? = entity.albumArtUri
        if (_tmpAlbumArtUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpAlbumArtUri)
        }
        val _tmpRelativePath: String? = entity.relativePath
        if (_tmpRelativePath == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRelativePath)
        }
        statement.bindLong(9, entity.size)
        statement.bindLong(10, entity.dateAdded)
        val _tmp: Int = if (entity.isFavorite) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.playCount.toLong())
        statement.bindLong(13, entity.lastPlayedTimestamp)
        statement.bindLong(14, entity.songId)
      }
    }
  }

  public override suspend fun insertSongs(songs: List<SongEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSongEntity.insert(_connection, songs)
  }

  public override suspend fun insertSong(song: SongEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSongEntity.insert(_connection, song)
  }

  public override suspend fun updateSong(song: SongEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfSongEntity.handle(_connection, song)
  }

  public override fun getAllSongs(): Flow<List<SongEntity>> {
    val _sql: String = "SELECT * FROM songs"
    return createFlow(__db, false, arrayOf("songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun getSongById(songId: Long): SongEntity? {
    val _sql: String = "SELECT * FROM songs WHERE songId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, songId)
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
        val _result: SongEntity?
        if (_stmt.step()) {
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
          _result = SongEntity(_tmpSongId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDuration,_tmpContentUri,_tmpAlbumArtUri,_tmpRelativePath,_tmpSize,_tmpDateAdded,_tmpIsFavorite,_tmpPlayCount,_tmpLastPlayedTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFavoriteSongs(): Flow<List<SongEntity>> {
    val _sql: String = "SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title ASC"
    return createFlow(__db, false, arrayOf("songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun getRecentlyPlayedSongs(): Flow<List<SongEntity>> {
    val _sql: String = "SELECT * FROM songs WHERE lastPlayedTimestamp > 0 ORDER BY lastPlayedTimestamp DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun getMostPlayedSongs(): Flow<List<SongEntity>> {
    val _sql: String = "SELECT * FROM songs WHERE playCount > 0 ORDER BY playCount DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun getRecentlyAddedSongs(): Flow<List<SongEntity>> {
    val _sql: String = "SELECT * FROM songs ORDER BY dateAdded DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("songs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun setFavorite(songId: Long, isFavorite: Boolean) {
    val _sql: String = "UPDATE songs SET isFavorite = ? WHERE songId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isFavorite) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, songId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun recordSongPlayed(songId: Long, timestamp: Long) {
    val _sql: String = "UPDATE songs SET playCount = playCount + 1, lastPlayedTimestamp = ? WHERE songId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, songId)
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
