package com.factupro.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InvoicesActivity : AppCompatActivity() {

    private lateinit var viewModel: InvoiceViewModel
    private lateinit var adapter: InvoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoices)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]

        adapter = InvoiceAdapter { invoice ->
            val options = arrayOf("✅ Payée", "⏳ En attente", "⚠️ En retard", "🖨️ Imprimer / PDF")
            AlertDialog.Builder(this)
                .setTitle("${invoice.numero} — ${invoice.clientNom}")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> viewModel.update(invoice.copy(statut = "Payée"))
                        1 -> viewModel.update(invoice.copy(statut = "En attente"))
                        2 -> viewModel.update(invoice.copy(statut = "En retard"))
                        3 -> imprimerFacture(invoice)
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewInvoices)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Filtres + recherche
        var toutesLesFactures = listOf<Invoice>()
        var filtreActif = "Toutes"

        fun appliquerFiltre() {
            val query = findViewById<android.widget.EditText>(R.id.etSearch).text.toString().trim().lowercase()
            var liste = toutesLesFactures
            if (filtreActif != "Toutes") liste = liste.filter { it.statut == filtreActif }
            if (query.isNotEmpty()) liste = liste.filter {
                it.clientNom.lowercase().contains(query) || it.numero.lowercase().contains(query)
            }
            adapter.setInvoices(liste)
        }

        viewModel.allInvoices.observe(this) { invoices ->
            toutesLesFactures = invoices
            appliquerFiltre()
        }

        findViewById<com.google.android.material.chip.Chip>(R.id.chipToutes).setOnClickListener {
            filtreActif = "Toutes"; appliquerFiltre()
        }
        findViewById<com.google.android.material.chip.Chip>(R.id.chipEnAttente).setOnClickListener {
            filtreActif = "En attente"; appliquerFiltre()
        }
        findViewById<com.google.android.material.chip.Chip>(R.id.chipPayee).setOnClickListener {
            filtreActif = "Payée"; appliquerFiltre()
        }
        findViewById<com.google.android.material.chip.Chip>(R.id.chipEnRetard).setOnClickListener {
            filtreActif = "En retard"; appliquerFiltre()
        }

        findViewById<android.widget.EditText>(R.id.etSearch).addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { appliquerFiltre() }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        findViewById<FloatingActionButton>(R.id.fabAddInvoice).setOnClickListener {
            startActivity(Intent(this, NewInvoiceActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_invoices
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_invoices -> true
                R.id.nav_clients -> {
                    startActivity(Intent(this, ClientsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun imprimerFacture(invoice: Invoice) {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.CANADA_FRENCH)

        val html = """
            <html>
            <head><meta charset="utf-8"><style>
                body { font-family: Arial, sans-serif; padding: 24px; color: #333; }
                .header { background: #0F3460; color: white; padding: 24px; border-radius: 8px; }
                .header h1 { margin: 0; font-size: 26px; }
                .header p { margin: 4px 0 0 0; color: #A0C4E8; }
                .info { margin-top: 24px; }
                .info td { padding: 6px 16px 6px 0; }
                .label { color: #888; font-size: 12px; text-transform: uppercase; }
                table.totaux { width: 100%; margin-top: 32px; border-collapse: collapse; }
                table.totaux td { padding: 10px; border-bottom: 1px solid #eee; }
                table.totaux td:last-child { text-align: right; }
                .total-final { background: #0F3460; color: white; font-weight: bold; font-size: 18px; }
                .statut { display: inline-block; padding: 6px 16px; border-radius: 16px; color: white; font-size: 13px;
                          background: ${if (invoice.statut == "Payée") "#4CAF50" else if (invoice.statut == "En retard") "#F44336" else "#FF6B35"}; }
                .footer { margin-top: 48px; color: #888; font-size: 11px; text-align: center; }
            </style></head>
            <body>
                <div class="header">
                    <h1>FactuPro</h1>
                    <p>Facture ${invoice.numero}</p>
                </div>
                <div class="info">
                    <table>
                        <tr><td class="label">Client</td><td><b>${invoice.clientNom}</b></td></tr>
                        <tr><td class="label">Date d'émission</td><td>${invoice.dateCreation}</td></tr>
                        <tr><td class="label">Date d'échéance</td><td>${invoice.dateEcheance}</td></tr>
                        <tr><td class="label">Statut</td><td><span class="statut">${invoice.statut}</span></td></tr>
                    </table>
                </div>
                <table class="totaux">
                    <tr><td>Sous-total (HT)</td><td>${formatter.format(invoice.sousTotal)}</td></tr>
                    <tr><td>TPS (5%)</td><td>${formatter.format(invoice.tps)}</td></tr>
                    <tr><td>TVQ (9,975%)</td><td>${formatter.format(invoice.tvq)}</td></tr>
                    <tr class="total-final"><td>TOTAL TTC</td><td>${formatter.format(invoice.total)}</td></tr>
                </table>
                <div class="footer">Facture générée par FactuPro — Merci de votre confiance</div>
            </body>
            </html>
        """.trimIndent()

        val webView = android.webkit.WebView(this)
        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: android.webkit.WebView, url: String) {
                val printManager = getSystemService(PRINT_SERVICE) as android.print.PrintManager
                val adapter = view.createPrintDocumentAdapter(invoice.numero)
                printManager.print(invoice.numero, adapter, android.print.PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}