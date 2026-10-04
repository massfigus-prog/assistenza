package com.massfigus.assistenza.data

import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.model.Intervento
import com.massfigus.assistenza.model.Macchina

class Repository(db: AppDatabase) {
    private val clienti = db.clienteDao()
    private val macchine = db.macchinaDao()
    private val interventi = db.interventoDao()

    // Clienti
    fun cliente(id: Long) = clienti.perId(id)
    suspend fun clienteOnce(id: Long) = clienti.perIdOnce(id)
    fun cercaClienti(testo: String) = clienti.cerca(testo)
    suspend fun inserisciCliente(c: Cliente) = clienti.inserisci(c)
    suspend fun aggiornaCliente(c: Cliente) = clienti.aggiorna(c)
    suspend fun eliminaCliente(c: Cliente) = clienti.elimina(c)

    // Macchine
    fun macchineDi(clienteId: Long) = macchine.perCliente(clienteId)
    fun cercaMacchine(testo: String) = macchine.cercaPerMatricola(testo)
    fun tutteLeMacchine() = macchine.tutteConCliente()
    suspend fun inserisciMacchina(m: Macchina) = macchine.inserisci(m)
    suspend fun eliminaMacchina(m: Macchina) = macchine.elimina(m)

    // Interventi
    fun intervento(id: Long) = interventi.dettaglio(id)
    suspend fun interventoOnce(id: Long) = interventi.dettaglioOnce(id)
    fun interventiAperti() = interventi.aperti()
    fun interventiDi(clienteId: Long) = interventi.perCliente(clienteId)
    suspend fun inserisciIntervento(i: Intervento) = interventi.inserisci(i)
    suspend fun aggiornaIntervento(i: Intervento) = interventi.aggiorna(i)
    suspend fun eliminaIntervento(i: Intervento) = interventi.elimina(i)
}
