package com.vashishth.vault.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PassManDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: password)

    @Delete
    suspend fun deletePassword(password: password)

    @Query("UPDATE password SET   password = :pass WHERE appName =:app")
    suspend fun updatePassword(pass: String,app : String)

    @Query("SELECT * FROM password")
    fun getPassList(): Flow<List<password>>
}