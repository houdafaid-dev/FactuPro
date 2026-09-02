package com.factupro.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class InvoiceAdapter(
    private val onInvoiceClick: (Invoice) -> Unit
) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    private var invoices = listOf<Invoice>()
    private val formatter = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH)

    class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumero    : TextView = itemView.findViewById(R.id.tvNumero)
        val tvClientNom : TextView = itemView.findViewById(R.id.tvClientNom)
        val tvDate      : TextView = itemView.findViewById(R.id.tvDate)
        val tvTotal     : TextView = itemView.findViewById(R.id.tvTotal)
        val tvStatut    : TextView = itemView.findViewById(R.id.tvStatut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.tvNumero.text    = invoice.numero
        holder.tvClientNom.text = invoice.clientNom
        holder.tvDate.text      = "📅 ${invoice.dateEcheance}"
        holder.tvTotal.text     = formatter.format(invoice.total)
        holder.tvStatut.text    = invoice.statut

        val (bgColor, textColor) = when (invoice.statut) {
            "Payée"     -> Pair(0xFF4CAF50.toInt(), 0xFFFFFFFF.toInt())
            "En retard" -> Pair(0xFFF44336.toInt(), 0xFFFFFFFF.toInt())
            else        -> Pair(0xFFFF6B35.toInt(), 0xFFFFFFFF.toInt())
        }
        holder.tvStatut.setBackgroundColor(bgColor)
        holder.tvStatut.setTextColor(textColor)

        holder.itemView.setOnClickListener { onInvoiceClick(invoice) }
    }

    override fun getItemCount() = invoices.size

    fun setInvoices(newList: List<Invoice>) {
        invoices = newList
        notifyDataSetChanged()
    }
}