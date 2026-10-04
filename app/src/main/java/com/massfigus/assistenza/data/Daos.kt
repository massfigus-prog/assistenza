package com.massfigus.assistenza.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.model.Intervento
import com.massfigus.assistenza.model.InterventoDettaglio
import com.massfigus.assistenza.model.Macchina
import com.massfigus.assistenza.model.MacchinaConCliente
import kotlinx.coroutines.flow.Flow

internal const val SELECT_MACCHINA_CLIENTE =
    "SELECT m.*, c.id AS c_id, c.nome AS c_nome, c.telefono AS c_telefono, " +
        "c.email AS c_email, c.indirizzo AS c_indirizzo " +
        "FROM macchine m INNER JOIN clienti c ON m.clienteId = c.id"

internal const val SELECT_DETTAGLIO =
    "SELECT i.*, m.id AS m_id, m.clienteId AS m_clienteId, m.modello AS m_modello, " +
        "m.matricola AS m_matricola, c.id AS c_id, c.nome AS c_nome, c.telefono AS c_telefono, " +
        "c.email AS c_email, c.indirizzo AS c_indirizzo " +
        "FROM interventi i " +
        "INNER JOIN macchine m ON i.macchinaId = m.id " +
        "INNER JOIN clienti c ON m.clienteId = c.id"

@Dao
interface ClienteDao {
    @Insert
    suspend fun inserisci(cliente: Cliente): Long

    @Update
    suspend fun aggiorna(cliente: Cliente)

    @Delete
    suspend fun elimina(cliente: Cliente)

    @Query("SELECT * FROM clienti WHERE id = :id")
    fun perId(id: Long): Flow<Cliente?>

    @Query("SELECT * FROM clienti WHERE id = :id")
    suspend fun perIdOnce(id: Long): Cliente?

    @Query("SELECT * FROM clienti WHERE nome LIKE '%' || :testo || '%' ORDER BY nome COLLATE NOCASE")
    fun cerca(testo: String): Flow<List<Cliente>>
}

@Dao
interface MacchinaDao {
    @Insert
    suspend fun inserisci(macchina: Macchina): Long

    @Delete
    suspend fun elimina(macchina: Macchina)

    @Query("SELECT * FROM macchine WHERE clienteId = :clienteId ORDER BY modello COLLATE NOCASE")
    fun perCliente(clienteId: Long): Flow<List<Macchina>>

    @Query(SELECT_MACCHINA_CLIENTE + " WHERE m.matricola LIKE '%' || :testo || '%' ORDER BY m.matricola")
    fun cercaPerMatricola(testo: String): Flow<List<MacchinaConCliente>>

    @Query(SELECT_MACCHINA_CLIENTE + " ORDER BY c.nome COLLATE NOCASE, m.modello COLLATE NOCASE")
    fun tutteConCliente(): Flow<List<MacchinaConCliente>>
}

@Dao
interface InterventoDao {
    @Insert
    suspend fun inserisci(intervento: Intervento): Long

    @Update
    suspend fun aggiorna(intervento: Intervento)

    @Delete
    suspend fun elimina(intervento: Intervento)

    @Query(SELECT_DETTAGLIO + " WHERE i.id = :id")
    fun dettaglio(id: Long): Flow<InterventoDettaglio?>

    @Query(SELECT_DETTAGLIO + " WHERE i.id = :id")
    suspend fun dettaglioOnce(id: Long): InterventoDettaglio?

    @Query(SELECT_DETTAGLIO + " WHERE i.stato = 'APERTO' ORDER BY i.data DESC LIMIT 50")
    fun aperti(): Flow<List<InterventoDettaglio>>

    @Query(SELECT_DETTAGLIO + " WHERE m.clienteId = :clienteId ORDER BY i.data DESC")
    fun perCliente(clienteId: Long): Flow<List<InterventoDettaglio>>
}
