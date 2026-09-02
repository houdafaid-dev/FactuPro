package com.factupro.app

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ClientAdapter(
    private val onClientClick: (Client) -> Unit = {}
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    private var clients = listOf<Client>()
    private var invoices = listOf<Invoice>()
    private val formatter = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH)

    private val couleurs = listOf(
        0xFF4CAF50.toInt(), // vert
        0xFFFF6B35.toInt(), // orange
        0xFF9C27B0.toInt(), // violet
        0xFFF44336.toInt(), // rouge
        0xFF2196F3.toInt(), // bleu
        0xFF009688.toInt()  // teal
    )

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvNomClient: TextView = itemView.findViewById(R.id.tvNomClient)
        val tvEmailClient: TextView = itemView.findViewById(R.id.tvEmailClient)
        val tvTotalClient: TextView = itemView.findViewById(R.id.tvTotalClient)
        val tvNbFactures: TextView = itemView.findViewById(R.id.tvNbFactures)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.tvNomClient.text = client.nom
        holder.tvEmailClient.text = client.email ?: ""
        holder.tvAvatar.text = client.nom.firstOrNull()?.uppercase() ?: "?"

        // Couleur d'avatar différente pour chaque client
        val bg = holder.tvAvatar.background.mutate() as GradientDrawable
        bg.setColor(couleurs[position % couleurs.size])

        // Total facturé + nombre de factures de ce client
        val facturesClient = invoices.filter { it.clientId == client.id }
        holder.tvTotalClient.text = formatter.format(facturesClient.sumOf { it.total })
        holder.tvNbFactures.text = "${facturesClient.size} facture(s)"
        holder.itemView.setOnClickListener { onClientClick(client) }
     }

    fun setClients(newList: List<Client>) {
        clients = newList
        notifyDataSetChanged()
    }

    fun setInvoices(newList: List<Invoice>) {
        invoices = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = clients.size
}