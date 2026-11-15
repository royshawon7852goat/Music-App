package com.pulse.music.`data`.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PulseDatabase_Impl : PulseDatabase() {
  private val _songDao: Lazy<SongDao> = lazy {
    SongDao_Impl(this)
  }

  private val _playlistDao: Lazy<PlaylistDao> = lazy {
    PlaylistDao_Impl(this)
  }

  private val _searchHistoryDao: Lazy<SearchHistoryDao> = lazy {
    SearchHistoryDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "c83c79647a60aeea55fd2c8132a0b503", "e43462018cc5e76445e1b84acc078528") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `songs` (`songId` INTEGER NOT NULL, `title` TEXT NOT NULL, `artist` TEXT NOT NULL, `album` TEXT NOT NULL, `duration` INTEGER NOT NULL, `contentUri` TEXT NOT NULL, `albumArtUri` TEXT, `relativePath` TEXT, `size` INTEGER NOT NULL, `dateAdded` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, `playCount` INTEGER NOT NULL, `lastPlayedTimestamp` INTEGER NOT NULL, PRIMARY KEY(`songId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`playlistId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isDefault` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `playlist_songs` (`playlistId` INTEGER NOT NULL, `songId` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `songId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`query` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`query`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c83c79647a60aeea55fd2c8132a0b503')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `songs`")
        connection.execSQL("DROP TABLE IF EXISTS `playlists`")
        connection.execSQL("DROP TABLE IF EXISTS `playlist_songs`")
        connection.execSQL("DROP TABLE IF EXISTS `search_history`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsSongs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSongs.put("songId", TableInfo.Column("songId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("artist", TableInfo.Column("artist", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("album", TableInfo.Column("album", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("duration", TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("contentUri", TableInfo.Column("contentUri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("albumArtUri", TableInfo.Column("albumArtUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("relativePath", TableInfo.Column("relativePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("size", TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("dateAdded", TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("isFavorite", TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("playCount", TableInfo.Column("playCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("lastPlayedTimestamp", TableInfo.Column("lastPlayedTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSongs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSongs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSongs: TableInfo = TableInfo("songs", _columnsSongs, _foreignKeysSongs, _indicesSongs)
        val _existingSongs: TableInfo = read(connection, "songs")
        if (!_infoSongs.equals(_existingSongs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |songs(com.pulse.music.data.database.SongEntity).
              | Expected:
              |""".trimMargin() + _infoSongs + """
              |
              | Found:
              |""".trimMargin() + _existingSongs)
        }
        val _columnsPlaylists: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlaylists.put("playlistId", TableInfo.Column("playlistId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylists.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylists.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylists.put("isDefault", TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlaylists: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlaylists: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlaylists: TableInfo = TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists)
        val _existingPlaylists: TableInfo = read(connection, "playlists")
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |playlists(com.pulse.music.data.database.PlaylistEntity).
              | Expected:
              |""".trimMargin() + _infoPlaylists + """
              |
              | Found:
              |""".trimMargin() + _existingPlaylists)
        }
        val _columnsPlaylistSongs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlaylistSongs.put("playlistId", TableInfo.Column("playlistId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylistSongs.put("songId", TableInfo.Column("songId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylistSongs.put("sortOrder", TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlaylistSongs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlaylistSongs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlaylistSongs: TableInfo = TableInfo("playlist_songs", _columnsPlaylistSongs, _foreignKeysPlaylistSongs, _indicesPlaylistSongs)
        val _existingPlaylistSongs: TableInfo = read(connection, "playlist_songs")
        if (!_infoPlaylistSongs.equals(_existingPlaylistSongs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |playlist_songs(com.pulse.music.data.database.PlaylistSongCrossRef).
              | Expected:
              |""".trimMargin() + _infoPlaylistSongs + """
              |
              | Found:
              |""".trimMargin() + _existingPlaylistSongs)
        }
        val _columnsSearchHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSearchHistory.put("query", TableInfo.Column("query", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSearchHistory.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSearchHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSearchHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSearchHistory: TableInfo = TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory)
        val _existingSearchHistory: TableInfo = read(connection, "search_history")
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |search_history(com.pulse.music.data.database.SearchHistoryEntity).
              | Expected:
              |""".trimMargin() + _infoSearchHistory + """
              |
              | Found:
              |""".trimMargin() + _existingSearchHistory)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "songs", "playlists", "playlist_songs", "search_history")
  }

  public override fun clearAllTables() {
    super.performClear(false, "songs", "playlists", "playlist_songs", "search_history")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(SongDao::class, SongDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PlaylistDao::class, PlaylistDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SearchHistoryDao::class, SearchHistoryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun songDao(): SongDao = _songDao.value

  public override fun playlistDao(): PlaylistDao = _playlistDao.value

  public override fun searchHistoryDao(): SearchHistoryDao = _searchHistoryDao.value
}
