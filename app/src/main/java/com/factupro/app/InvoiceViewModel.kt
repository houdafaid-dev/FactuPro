package com.factupro.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val invoiceDao = FactuProDatabase.getDatabase(application).invoiceDao()

    val allInvoices: LiveData<List<Invoice>> = invoiceDao.getAllInvoices()
    val totalInvoices: LiveData<Int> = invoiceDao.getTotalInvoices()
    val totalRevenu: LiveData<Double> = invoiceDao.getTotalRevenu()

    // Calcul TPS et TVQ
    fun calculerTPS(sousTotal: Double): Double = sousTotal * 0.05
    fun calculerTVQ(sousTotal: Double): Double = sousTotal * 0.09975
    fun calculerTotal(sousTotal: Double): Double {
        return sousTotal + calculerTPS(sousTotal) + calculerTVQ(sousTotal)
    }

    // Générer numéro de facture automatique
    suspend fun genererNumero(): String {
        val derniere = invoiceDao.getLastInvoice()
        val annee = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        return if (derniere == null) {
            "FAC-$annee-001"
        } else {
            val num = derniere.numero.substringAfterLast("-").toIntOrNull() ?: 0
            "FAC-$annee-${String.format("%03d", num + 1)}"
        }
    }

    fun insert(invoice: Invoice) = viewModelScope.launch {
        invoiceDao.insert(invoice)
    }

    fun update(invoice: Invoice) = viewModelScope.launch {
        invoiceDao.update(invoice)
    }

    fun delete(invoice: Invoice) = viewModelScope.launch {
        invoiceDao.delete(invoice)
    }

    fun getInvoicesByClient(clientId: Int) = invoiceDao.getInvoicesByClient(clientId)
}