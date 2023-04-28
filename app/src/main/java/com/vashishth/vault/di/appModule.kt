package com.vashishth.vault.di

import android.content.Context
import androidx.room.Room
import com.vashishth.vault.db.PassDatabase
import com.vashishth.vault.db.PassManDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): PassDatabase {
        return Room.databaseBuilder(context, PassDatabase::class.java, "PasswordDatabase")
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun providesDao(passDatabase: PassDatabase) : PassManDao {
        return passDatabase.getPassManDao()
    }
}