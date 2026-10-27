package com.contascaseiras.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.contascaseiras.app.data.local.converters.Converters
import com.contascaseiras.app.data.local.dao.TransacaoDao
import com.contascaseiras.app.data.local.entity.TransacaoEntity

@Database(
    entities = [TransacaoEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transacaoDao(): TransacaoDao

    companion object {
        const val DATABASE_NAME = "contas_caseiras.db"
    }
}
