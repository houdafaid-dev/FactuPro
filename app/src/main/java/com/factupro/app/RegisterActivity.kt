package com.factupro.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNom        = findViewById<TextInputEditText>(R.id.etNom)
        val etEmail      = findViewById<TextInputEditText>(R.id.etEmail)
        val etEntreprise = findViewById<TextInputEditText>(R.id.etEntreprise)
        val etPassword   = findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister  = findViewById<MaterialButton>(R.id.btnRegister)
        val tvError      = findViewById<TextView>(R.id.tvError)
        val tvLogin      = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {
            val nom        = etNom.text.toString().trim()
            val email      = etEmail.text.toString().trim()
            val entreprise = etEntreprise.text.toString().trim()
            val mdp        = etPassword.text.toString().trim()

            if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
                tvError.text = "Veuillez remplir les champs obligatoires"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (mdp.length < 6) {
                tvError.text = "Le mot de passe doit contenir au moins 6 caractères"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            authViewModel.register(nom, email, mdp, entreprise)
        }

        authViewModel.registerResult.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        authViewModel.errorMessage.observe(this) { msg ->
            tvError.text = msg
            tvError.visibility = View.VISIBLE
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }
}