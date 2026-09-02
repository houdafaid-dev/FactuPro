package com.factupro.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = FactuProDatabase.getDatabase(application).userDao()

    val loginResult = MutableLiveData<User?>()
    val registerResult = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, motDePasse: String) {
        viewModelScope.launch {
            val user = userDao.login(email, motDePasse)
            if (user != null) {
                loginResult.postValue(user)
            } else {
                errorMessage.postValue("Email ou mot de passe incorrect")
            }
        }
    }

    fun register(nom: String, email: String, motDePasse: String, entreprise: String) {
        viewModelScope.launch {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                errorMessage.postValue("Cet email est déjà utilisé")
                return@launch
            }
            val user = User(
                nom = nom,
                email = email,
                motDePasse = motDePasse,
                entreprise = entreprise
            )
            userDao.insert(user)
            registerResult.postValue(true)
        }
    }
}