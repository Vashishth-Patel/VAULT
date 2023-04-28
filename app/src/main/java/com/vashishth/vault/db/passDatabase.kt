package com.vashishth.vault.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [password::class],
    version = 1
)
abstract class PassDatabase : RoomDatabase() {
    abstract fun getPassManDao(): PassManDao
}