package com.factupro.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClientRepository
    val allClients: LiveData<List<Client>>

    init {
        val clientDao = FactuProDatabase.getDatabase(application).clientDao()
        repository = ClientRepository(clientDao)
        allClients = repository.allClients
    }

    fun insert(client: Client) = viewModelScope.launch {
        repository.insert(client)
    }

    fun update(client: Client) = viewModelScope.launch {
        repository.update(client)
    }

    fun delete(client: Client) = viewModelScope.launch {
        repository.delete(client)
    }
}