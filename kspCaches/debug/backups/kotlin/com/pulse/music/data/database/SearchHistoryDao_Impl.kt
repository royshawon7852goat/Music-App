package com.pulse.music.`data`.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
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
public class SearchHistoryDao_Impl(
  __db: RoomDatabase,
) : SearchHistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSearchHistoryEntity: EntityInsertAdapter<SearchHistoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSearchHistoryEntity = object : EntityInsertAdapter<SearchHistoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `search_history` (`query`,`timestamp`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SearchHistoryEntity) {
        statement.bindText(1, entity.query)
        statement.bindLong(2, entity.timestamp)
      }
    }
  }

  public override suspend fun insertQuery(entity: SearchHistoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSearchHistoryEntity.insert(_connection, entity)
  }

  public override fun getRecentQueries(): Flow<List<String>> {
    val _sql: String = "SELECT query FROM search_history ORDER BY timestamp DESC LIMIT 20"
    return createFlow(__db, false, arrayOf("search_history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteQuery(query: String) {
    val _sql: String = "DELETE FROM search_history WHERE query = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearHistory() {
    val _sql: String = "DELETE FROM search_history"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
