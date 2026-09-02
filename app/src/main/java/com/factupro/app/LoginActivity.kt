package com.factupro.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail    = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin   = findViewById<MaterialButton>(R.id.btnLogin)
        val tvError    = findViewById<TextView>(R.id.tvError)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)


        // Connexion
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val mdp   = etPassword.text.toString().trim()

            if (email.isEmpty() || mdp.isEmpty()) {
                tvError.text = "Veuillez remplir tous les champs"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            authViewModel.login(email, mdp)
        }

        // Résultat connexion
        authViewModel.loginResult.observe(this) { user ->
            if (user != null) {
                // Sauvegarder le nom de l'utilisateur
                val prefs = getSharedPreferences("factupro_prefs", MODE_PRIVATE)
                prefs.edit().putString("user_nom", user.nom).apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // Message d'erreur
        authViewModel.errorMessage.observe(this) { msg ->
            tvError.text = msg
            tvError.visibility = View.VISIBLE
        }

        // Aller à l'inscription
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}