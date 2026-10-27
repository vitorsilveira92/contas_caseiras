package com.contascaseiras.app.di

import android.content.Context
import androidx.room.Room
import com.contascaseiras.app.data.local.AppDatabase
import com.contascaseiras.app.data.local.dao.TransacaoDao
import com.contascaseiras.app.data.repository.TransacaoRepository
import com.contascaseiras.app.data.repository.TransacaoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideTransacaoDao(database: AppDatabase): TransacaoDao = database.transacaoDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTransacaoRepository(impl: TransacaoRepositoryImpl): TransacaoRepository
}
