package com.factupro.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numero: String,
    val clientId: Int,
    val clientNom: String,
    val dateCreation: String,
    val dateEcheance: String,
    val sousTotal: Double,
    val tps: Double,
    val tvq: Double,
    val total: Double,
    val statut: String = "En attente",
    val notes: String? = null
)