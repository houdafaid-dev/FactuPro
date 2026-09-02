package com.factupro.app

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ClientDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_detail)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clientId = intent.getIntExtra("client_id", -1)
        val formatter = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH)

        val clientViewModel = ViewModelProvider(this)[ClientViewModel::class.java]
        val invoiceViewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]

        val historiqueAdapter = InvoiceAdapter { }
        val rv = findViewById<RecyclerView>(R.id.recyclerViewHistorique)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = historiqueAdapter

        clientViewModel.allClients.observe(this) { clients ->
            val client = clients.find { it.id == clientId } ?: return@observe
            findViewById<TextView>(R.id.tvNomDetail).text = client.nom
            findViewById<TextView>(R.id.tvAvatarDetail).text =
                client.nom.firstOrNull()?.uppercase() ?: "?"
            findViewById<TextView>(R.id.tvContactDetail).text =
                listOfNotNull(client.email, client.telephone).joinToString(" • ")
            findViewById<TextView>(R.id.tvEmailDetail).text = "📧 ${client.email ?: "—"}"
            findViewById<TextView>(R.id.tvTelephoneDetail).text = "📞 ${client.telephone ?: "—"}"
            val adresse = listOfNotNull(client.adresse, client.ville).joinToString(", ")
            findViewById<TextView>(R.id.tvAdresseDetail).text = "📍 ${adresse.ifEmpty { "—" }}"
        }

        invoiceViewModel.allInvoices.observe(this) { invoices ->
            val facturesClient = invoices.filter { it.clientId == clientId }
            findViewById<TextView>(R.id.tvCaTotal).text =
                formatter.format(facturesClient.sumOf { it.total })
            findViewById<TextView>(R.id.tvNbFacturesDetail).text =
                facturesClient.size.toString()
            findViewById<TextView>(R.id.tvPayees).text =
                facturesClient.count { it.statut == "Payée" }.toString()
            historiqueAdapter.setInvoices(facturesClient)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}