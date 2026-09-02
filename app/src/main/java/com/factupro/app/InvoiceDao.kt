package com.factupro.app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InvoiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: Invoice)

    @Update
    suspend fun update(invoice: Invoice)

    @Delete
    suspend fun delete(invoice: Invoice)

    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun getAllInvoices(): LiveData<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE clientId = :clientId")
    fun getInvoicesByClient(clientId: Int): LiveData<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE statut = 'En attente'")
    fun getInvoicesEnAttente(): LiveData<List<Invoice>>

    @Query("SELECT COUNT(*) FROM invoices")
    fun getTotalInvoices(): LiveData<Int>

    @Query("SELECT SUM(total) FROM invoices WHERE statut = 'Payée'")
    fun getTotalRevenu(): LiveData<Double>

    @Query("SELECT * FROM invoices ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoice(): Invoice?
}