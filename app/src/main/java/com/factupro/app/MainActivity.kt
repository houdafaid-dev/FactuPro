package com.factupro.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var clientViewModel: ClientViewModel
    private lateinit var invoiceViewModel: InvoiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Afficher le nom de l'utilisateur
        val prefs = getSharedPreferences("factupro_prefs", MODE_PRIVATE)
        val nom = prefs.getString("user_nom", "")
        findViewById<TextView>(R.id.tvWelcome).text = "Bonjour, $nom 👋"
        clientViewModel  = ViewModelProvider(this)[ClientViewModel::class.java]
        invoiceViewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]

        val formatter = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH)
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_invoices -> {
                    startActivity(Intent(this, InvoicesActivity::class.java))
                    true
                }
                R.id.nav_clients -> {
                    startActivity(Intent(this, ClientsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        // Stats clients
        clientViewModel.allClients.observe(this) { clients ->
            findViewById<TextView>(R.id.tvClientsActifs).text = clients.size.toString()
        }

        // Stats factures
        invoiceViewModel.allInvoices.observe(this) { invoices ->
            findViewById<TextView>(R.id.tvFacturesCount).text = invoices.size.toString()

            val enAttente = invoices.filter { it.statut == "En attente" }
            val montantAttente = enAttente.sumOf { it.total }
            val caTotal = invoices.sumOf { it.total }

            findViewById<TextView>(R.id.tvCaMois).text    = formatter.format(caTotal)
            findViewById<TextView>(R.id.tvEnAttente).text = formatter.format(montantAttente)
            findViewById<TextView>(R.id.tvEnAttenteCount).text =
                "${enAttente.size} facture(s) impayée(s)"
        }
        // Graphique CA 6 derniers mois
        invoiceViewModel.allInvoices.observe(this) { invoices ->
            val moisLabels = listOf("Fév", "Mar", "Avr", "Mai", "Juin", "Juil")
            val bars = listOf(
                findViewById<android.view.View>(R.id.bar1),
                findViewById(R.id.bar2),
                findViewById(R.id.bar3),
                findViewById(R.id.bar4),
                findViewById(R.id.bar5),
                findViewById(R.id.bar6)
            )
            val labels = listOf(
                findViewById<TextView>(R.id.barLabel1),
                findViewById(R.id.barLabel2),
                findViewById(R.id.barLabel3),
                findViewById(R.id.barLabel4),
                findViewById(R.id.barLabel5),
                findViewById(R.id.barLabel6)
            )
            labels.forEachIndexed { i, tv -> tv.text = moisLabels[i] }

            // Total simple réparti sur le dernier mois (démo)
            val total = invoices.sumOf { it.total }
            val maxH = 100  // dp max
            val density = resources.displayMetrics.density
            bars.forEachIndexed { i, bar ->
                val h = if (i == bars.size - 1 && total > 0) maxH else (15 + i * 10)
                bar.layoutParams.height = (h * density).toInt()
                bar.requestLayout()
            }
        }

        // Navigation
        findViewById<CardView>(R.id.cardClients).setOnClickListener {
            startActivity(Intent(this, ClientsActivity::class.java))
        }
        findViewById<CardView>(R.id.cardFactures).setOnClickListener {
            startActivity(Intent(this, NewInvoiceActivity::class.java))
        }
        findViewById<FloatingActionButton>(R.id.fabNewInvoice).setOnClickListener {
            startActivity(Intent(this, NewInvoiceActivity::class.java))
        }

        // Réglages ⚙️
        findViewById<TextView>(R.id.btnSettings).setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Réglages")
                .setItems(arrayOf("👤 Mon profil", "🚪 Déconnexion")) { _, which ->
                    when (which) {
                        0 -> {
                            android.widget.Toast.makeText(this, "Connecté en tant que : $nom", android.widget.Toast.LENGTH_LONG).show()
                        }
                        1 -> {
                            getSharedPreferences("factupro_prefs", MODE_PRIVATE)
                                .edit().clear().apply()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
                .setNegativeButton("Fermer", null)
                .show()
        }
    }  // ← fin de onCreate
        }
