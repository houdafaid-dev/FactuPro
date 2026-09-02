package com.factupro.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nom: String,
    val email: String? = null,
    val telephone: String? = null,
    val adresse: String? = null,
    val ville: String? = null,
    val codePostal: String? = null
)