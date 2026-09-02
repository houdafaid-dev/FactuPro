package com.factupro.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewInvoiceActivity : AppCompatActivity() {

    private lateinit var invoiceViewModel: InvoiceViewModel
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var containerLignes: LinearLayout
    private var clients = listOf<Client>()
    private val formatter = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.CANADA_FRENCH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_invoice)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        invoiceViewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]
        clientViewModel = ViewModelProvider(this)[ClientViewModel::class.java]
        containerLignes = findViewById(R.id.containerLignes)

        // Spinner clients
        val spinner = findViewById<Spinner>(R.id.spinnerClient)
        clientViewModel.allClients.observe(this) { list ->
            clients = list
            val noms = list.map { it.nom }
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, noms)
        }

        // Numéro auto
        val tvNumero = findViewById<TextView>(R.id.tvNumeroFacture)
        invoiceViewModel.allInvoices.observe(this) { invoices ->
            val annee = Calendar.getInstance().get(Calendar.YEAR)
            tvNumero.text = "FAC-$annee-%03d".format(invoices.size + 1)
        }

        // Dates
        val tvEmission = findViewById<TextView>(R.id.tvDateEmission)
        val tvEcheance = findViewById<TextView>(R.id.tvDateEcheance)
        val cal = Calendar.getInstance()
        tvEmission.text = dateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 30)
        tvEcheance.text = dateFormat.format(cal.time)

        tvEmission.setOnClickListener { pickDate(tvEmission) }
        tvEcheance.setOnClickListener { pickDate(tvEcheance) }

        // Lignes
        findViewById<TextView>(R.id.btnAjouterLigne).setOnClickListener { ajouterLigne() }
        ajouterLigne() // première ligne par défaut

        // Enregistrer
        findViewById<Button>(R.id.btnEnregistrer).setOnClickListener { enregistrerFacture() }
    }

    private fun pickDate(target: TextView) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            target.text = dateFormat.format(cal.time)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun ajouterLigne() {
        val ligne = LayoutInflater.from(this).inflate(R.layout.item_ligne_facture, containerLignes, false)
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculerTotaux() }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        }
        ligne.findViewById<EditText>(R.id.etQuantite).addTextChangedListener(watcher)
        ligne.findViewById<EditText>(R.id.etPrix).addTextChangedListener(watcher)
        ligne.findViewById<TextView>(R.id.btnSupprimer).setOnClickListener {
            containerLignes.removeView(ligne)
            calculerTotaux()
        }
        containerLignes.addView(ligne)
    }

    private fun sousTotal(): Double {
        var total = 0.0
        for (i in 0 until containerLignes.childCount) {
            val ligne = containerLignes.getChildAt(i)
            val qte = ligne.findViewById<EditText>(R.id.etQuantite).text.toString().toIntOrNull() ?: 0
            val prix = ligne.findViewById<EditText>(R.id.etPrix).text.toString().toDoubleOrNull() ?: 0.0
            total += qte * prix
        }
        return total
    }

    private fun calculerTotaux() {
        val st = sousTotal()
        val tps = st * 0.05
        val tvq = st * 0.09975
        findViewById<TextView>(R.id.tvSousTotal).text = formatter.format(st)
        findViewById<TextView>(R.id.tvTps).text = formatter.format(tps)
        findViewById<TextView>(R.id.tvTvq).text = formatter.format(tvq)
        findViewById<TextView>(R.id.tvTotalTtc).text = formatter.format(st + tps + tvq)
    }

    private fun enregistrerFacture() {
        val spinner = findViewById<Spinner>(R.id.spinnerClient)
        if (clients.isEmpty()) {
            Toast.makeText(this, "Ajoutez d'abord un client", Toast.LENGTH_SHORT).show()
            return
        }
        val st = sousTotal()
        if (st <= 0) {
            Toast.makeText(this, "Ajoutez au moins une ligne avec un prix", Toast.LENGTH_SHORT).show()
            return
        }
        val client = clients[spinner.selectedItemPosition]
        val tps = st * 0.05
        val tvq = st * 0.09975

        invoiceViewModel.insert(Invoice(
            numero       = findViewById<TextView>(R.id.tvNumeroFacture).text.toString(),
            clientId     = client.id,
            clientNom    = client.nom,
            dateCreation = findViewById<TextView>(R.id.tvDateEmission).text.toString(),
            dateEcheance = findViewById<TextView>(R.id.tvDateEcheance).text.toString(),
            sousTotal    = st,
            tps          = tps,
            tvq          = tvq,
            total        = st + tps + tvq
        ))
        Toast.makeText(this, "Facture enregistrée !", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}