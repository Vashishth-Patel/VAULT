package com.vashishth.vault.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class password(
    @PrimaryKey
    val appName: String,
    val password: String
)