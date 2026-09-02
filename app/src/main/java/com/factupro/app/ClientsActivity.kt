package com.factupro.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ClientsActivity : AppCompatActivity() {

    private lateinit var viewModel: ClientViewModel
    private lateinit var adapter: ClientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ClientViewModel::class.java]

        adapter = ClientAdapter { client ->
            val intent = Intent(this, ClientDetailActivity::class.java)
            intent.putExtra("client_id", client.id)
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewClients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Liste + recherche
        var tousLesClients = listOf<Client>()
        val etSearch = findViewById<android.widget.EditText>(R.id.etSearchClient)

        viewModel.allClients.observe(this) { clients ->
            tousLesClients = clients
            adapter.setClients(clients)
        }

        val invoiceViewModel = ViewModelProvider(this)[InvoiceViewModel::class.java]
        invoiceViewModel.allInvoices.observe(this) { invoices ->
            adapter.setInvoices(invoices)
        }

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().trim().lowercase()
                adapter.setClients(
                    if (query.isEmpty()) tousLesClients
                    else tousLesClients.filter { it.nom.lowercase().contains(query) }
                )
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        val fab = findViewById<FloatingActionButton>(R.id.fabAddClient)
        fab.setOnClickListener { showAddClientDialog() }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_clients
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_invoices -> {
                    startActivity(Intent(this, InvoicesActivity::class.java))
                    true
                }
                R.id.nav_clients -> true
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showAddClientDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_client, null)

        val etNom       = dialogView.findViewById<TextInputEditText>(R.id.etNom)
        val etEmail     = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val etTelephone = dialogView.findViewById<TextInputEditText>(R.id.etTelephone)
        val etAdresse   = dialogView.findViewById<TextInputEditText>(R.id.etAdresse)
        val etVille     = dialogView.findViewById<TextInputEditText>(R.id.etVille)

        AlertDialog.Builder(this)
            .setTitle("Nouveau client")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val nom = etNom.text.toString().trim()
                if (nom.isEmpty()) {
                    Toast.makeText(this, "Le nom est obligatoire", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.insert(Client(
                    nom       = nom,
                    email     = etEmail.text.toString().trim().ifEmpty { null },
                    telephone = etTelephone.text.toString().trim().ifEmpty { null },
                    adresse   = etAdresse.text.toString().trim().ifEmpty { null },
                    ville     = etVille.text.toString().trim().ifEmpty { null }
                ))
                Toast.makeText(this, "Client ajouté !", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}