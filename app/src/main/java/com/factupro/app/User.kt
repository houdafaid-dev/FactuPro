package com.factupro.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val email: String,
    val motDePasse: String,
    val entreprise: String? = null,
    val telephone: String? = null
)