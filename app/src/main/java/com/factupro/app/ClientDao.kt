package com.factupro.app

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY nom ASC")
    fun getAllClients(): LiveData<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Delete
    suspend fun delete(client: Client)

    @Query("SELECT * FROM clients WHERE id = :id")
    fun getClientById(id: Int): LiveData<Client>
}
